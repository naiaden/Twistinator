/**
 * 
 */
package nl.naiaden.twistinator.indexer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import nl.naiaden.twistinator.Application;
import nl.naiaden.twistinator.analysis.PatternAnalyzer;
import nl.naiaden.twistinator.indexer.document.DocumentId;
import nl.naiaden.twistinator.indexer.document.Keyword;
import nl.naiaden.twistinator.indexer.document.ParentId;
import nl.naiaden.twistinator.indexer.document.Triple;
import nl.naiaden.twistinator.indexer.input.AsynchronousSentsReader;
import nl.naiaden.twistinator.indexer.input.Reader;
import nl.naiaden.twistinator.indexer.input.ReaderFactory;
import nl.naiaden.twistinator.indexer.input.Sent;
import nl.naiaden.twistinator.indexer.input.Sents;
import nl.naiaden.twistinator.indexer.input.Text;
import nl.naiaden.twistinator.indexer.output.AsynchronousIndexerWriter;
import nl.naiaden.twistinator.objects.Returnable;
import nl.naiaden.twistinator.objects.SearchQuery;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author louis
 * 
 */
public class Index
{
	static Logger log = Logger.getLogger(Index.class);

	public static String generateSentId()
	{
		return RandomStringUtils.randomAlphanumeric(10);
	}
	public static String generateTextId()
	{
		return RandomStringUtils.randomAlphanumeric(10);
	}
	private File indexFile;
	private Directory indexDirectory;
	private TextRegister textRegister;

	private Class<? extends Reader> readerClass;

	private Class<?> writerClass;
	private boolean ignoreId;
	public static final String FIELD_ID = "id";
	public static final String FIELD_PARENTID = "parentId";

	public static final String FIELD_TRIPLES = "triples";

	public static final String FIELD_SENTENCE = "sentence";

	public Index(File indexFile) throws IOException
	{
		this(indexFile, AsynchronousSentsReader.class);
	}

	public Index(File indexFile, Class<? extends Reader> reader) throws IOException
	{
		this(indexFile, reader, AsynchronousIndexerWriter.class);
	}

	public Index(File indexFile, Class<? extends Reader> reader, Class<?> writer) throws IOException
	{
		this.indexFile = indexFile;
		indexDirectory = FSDirectory.open(indexFile);
		readerClass = reader;
		writerClass = writer;
	}

	public void addToIndex(File inputFile)
	{
		try
		{
			//			Directory directory = FSDirectory.open(indexFile);

			Pattern tripleSplitter = Triple.tripleSplitter;
			CharArraySet noStopWords = new CharArraySet(Version.LUCENE_34, 0, false);

			PerFieldAnalyzerWrapper pfaWrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_33));
			Analyzer tripleAnalyzer = new PatternAnalyzer(Version.LUCENE_33, tripleSplitter, false, noStopWords);
			pfaWrapper.addAnalyzer("triples", tripleAnalyzer);

			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_33, pfaWrapper);
			indexWriterConfig.setIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());

			long startBuilding = System.nanoTime();
			IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);

			LinkedBlockingQueue<Document> queue = new LinkedBlockingQueue<Document>(10000);

			Reader reader = ReaderFactory.create(readerClass, inputFile, queue);
			reader.setIgnoreId(ignoreId);
			Thread readerThread = new Thread(reader, "AsynReader");
			readerThread.start();

			AsynchronousIndexerWriter writer = new AsynchronousIndexerWriter(indexWriter, queue, 100);
			Thread writerThread = new Thread(writer, "AsyncWriter");
			writerThread.start();
			//			AsynchronousIndexerWriter writer1 = new AsynchronousIndexerWriter(indexWriter, queue, 100);
			//			Thread writerThread1 = new Thread(writer1, "AsyncWriter1");
			//			writerThread1.start();
			//			AsynchronousIndexerWriter writer2 = new AsynchronousIndexerWriter(indexWriter, queue, 100);
			//			Thread writerThread2 = new Thread(writer2, "AsyncWriter2");
			//			writerThread2.start();
			//			AsynchronousIndexerWriter writer3 = new AsynchronousIndexerWriter(indexWriter, queue, 100);
			//			Thread writerThread3 = new Thread(writer3, "AsyncWriter3");
			//			writerThread3.start();


			try
			{
				readerThread.join();
				textRegister = reader.getTextRegister();
				log.info(textRegister.size() + " documents!");
				writer.keepRunning = false;
				//				writer1.keepRunning = false;
				//				writer2.keepRunning = false;
				//				writer3.keepRunning = false;
				writerThread.join();
				//				writerThread1.join();
				//				writerThread2.join();
				//				writerThread3.join();

				writer.optimize();
				writer.close();


				for(Text text : textRegister.values())
				{
					log.debug(text.toString());
				}

				Application.logTiming(log, "Building index took " + (System.nanoTime() - startBuilding) * 1e-9 + " seconds");
				log.info("Index size: " + FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(indexFile)));

			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() throws IOException
	{
		indexDirectory.close();
	}

	public void createIndex(File inputFile)
	{
		removeIndex();
		addToIndex(inputFile);
	}

	public void removeIndex()
	{
		try
		{
			textRegister = new TextRegister();

			if(indexFile.exists())
			{
				log.info("Removing index: " + indexFile.getAbsolutePath());
				FileUtils.cleanDirectory(indexFile);
			} else
			{
				log.info("Cannot clean " + indexFile.getAbsolutePath() + " because it does not exist");
			}
		} catch (IOException e)
		{
			log.error("Encountered error whilst cleaning index: " + e.getMessage());
		}
	}

	public Returnable searchIndex(SearchQuery searchQuery, int numberOfResults)
	{
		if(searchQuery.query instanceof Triple)
		{
			Triple triple = (Triple) searchQuery.query;
			return searchIndexForTriple(triple, numberOfResults);
		}
		if(searchQuery.query instanceof Keyword)
		{
			Keyword keyword = (Keyword) searchQuery.query;
			return searchIndexForKeyword(keyword, numberOfResults);
		}
		if(searchQuery.query instanceof DocumentId)
		{
			DocumentId documentId = (DocumentId) searchQuery.query;
			return searchIndexForDocumentId(documentId, numberOfResults);
		}
		if(searchQuery.query instanceof ParentId)
		{
			ParentId parentId = (ParentId) searchQuery.query;
			return searchIndexForDocumentId(parentId, numberOfResults);
		}
		log.error("Cannot use '" + searchQuery.toString() + "' to search in the index");
		return null;
	}

	/**
	 * @param documentId
	 * @param numberOfResults
	 * @return
	 */
	public Returnable searchIndexForDocumentId(DocumentId documentId, int numberOfResults)
	{
		String searchField = FIELD_ID;
		if(documentId instanceof ParentId)
		{
			searchField = FIELD_PARENTID;
		}

		try
		{
			IndexReader indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			Analyzer normalAnalyzer = new StandardAnalyzer(Version.LUCENE_34);
			QueryParser queryParser = new QueryParser(Version.LUCENE_34, searchField, normalAnalyzer);
			Query nQuery = queryParser.parse(documentId.getDocumentId());

			ScoreDoc[] hits = indexSearcher.search(nQuery, null, numberOfResults).scoreDocs;
			log.info("Number of hits for document id '" + documentId + "': " + hits.length);
			//			log.info("Number of hits for '" + word + "' in sentence: " + hits.length + " (showing first " + numberOfResults + " documents)");

			Sents sents = null;

			if(hits.length > 0)
			{

				sents = new Sents();

				for(ScoreDoc hit : hits)
				{
					Document d = indexSearcher.doc(hit.doc);
					//				sents.add(textRegister.get(d.get(FIELD_PARENTID)));
					sents.add(new Sent(d));
				}
			}

			return sents;

			/*
			 * For paginated results see
			 * http://hrycan.com/2010/02/10/paginating-lucene-search-results/
			 */
			//			for(ScoreDoc scoreDoc : nTopDocs.scoreDocs)
			//			{
			//				Document doc = indexSearcher.doc(scoreDoc.doc);
			//				System.out.printf("[%5.3f] %s\n", scoreDoc.score, doc.get(FIELD_SENTENCE));
			//			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.warn("Searching index for keywords failed!");
		return null;
	}

	public Returnable searchIndexForKeyword(Keyword word, int numberOfResults)
	{
		try
		{
			IndexReader indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			Analyzer normalAnalyzer = new StandardAnalyzer(Version.LUCENE_34);
			QueryParser queryParser = new QueryParser(Version.LUCENE_34, FIELD_SENTENCE, normalAnalyzer);
			Query nQuery = queryParser.parse(word.getKeyword());

			ScoreDoc[] hits = indexSearcher.search(nQuery, null, numberOfResults).scoreDocs;
			log.info("Number of hits for '" + word + "' in sentence: " + hits.length);
			//			log.info("Number of hits for '" + word + "' in sentence: " + hits.length + " (showing first " + numberOfResults + " documents)");

			Sents sents = null;

			if(hits.length > 0)
			{

				sents = new Sents();

				for(ScoreDoc hit : hits)
				{
					Document d = indexSearcher.doc(hit.doc);
					//				sents.add(textRegister.get(d.get(FIELD_PARENTID)));
					sents.add(new Sent(d));
				}
			}

			return sents;

			/*
			 * For paginated results see
			 * http://hrycan.com/2010/02/10/paginating-lucene-search-results/
			 */
			//			for(ScoreDoc scoreDoc : nTopDocs.scoreDocs)
			//			{
			//				Document doc = indexSearcher.doc(scoreDoc.doc);
			//				System.out.printf("[%5.3f] %s\n", scoreDoc.score, doc.get(FIELD_SENTENCE));
			//			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.warn("Searching index for keywords failed!");
		return null;
	}

	public Returnable searchIndexForTriple(Triple triple, int numberOfResults)
	{
		try
		{
			IndexReader indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			Query tQuery = null;

			// Wildcard queries can be much slower as they need to iterate over many terms.
			// Unless a query really contains a wildcard, we do it the normal way
			if(triple.containsWildcard())
			{
				tQuery = new WildcardQuery(new Term(FIELD_TRIPLES, triple.toString(false)));
			} else
			{
				tQuery = new TermQuery(new Term(FIELD_TRIPLES, triple.toString(false)));
			}

			ScoreDoc[] hits = indexSearcher.search(tQuery, null, 1000).scoreDocs;
			log.info("Number of hits for '" + triple + "' in triples: " + hits.length);

			Sents sents = null;

			if(hits.length > 0)
			{
				sents = new Sents();

				for(ScoreDoc hit : hits)
				{
					Document d = indexSearcher.doc(hit.doc);
					//				sents.add(textRegister.get(d.get(FIELD_PARENTID)));
					sents.add(new Sent(d));
				}
			}

			return sents;

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.warn("Searching index for triples failed!");
		return null;
	}

	public void searchIndexForWord(String word, int numberOfResults)
	{
		try
		{
			IndexReader indexReader = IndexReader.open(indexDirectory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			Analyzer normalAnalyzer = new StandardAnalyzer(Version.LUCENE_34);
			QueryParser queryParser = new QueryParser(Version.LUCENE_34, FIELD_SENTENCE, normalAnalyzer);
			Query nQuery = queryParser.parse(word);

			TopDocs nTopDocs = indexSearcher.search(nQuery, null, numberOfResults);
			log.info("Number of hits for '" + word + "' in sentence: " + nTopDocs.totalHits + " (showing first " + numberOfResults + " documents)");

			/*
			 * For paginated results see
			 * http://hrycan.com/2010/02/10/paginating-lucene-search-results/
			 */
			for(ScoreDoc scoreDoc : nTopDocs.scoreDocs)
			{
				Document doc = indexSearcher.doc(scoreDoc.doc);
				System.out.printf("[%5.3f] %s\n", scoreDoc.score, doc.get(FIELD_SENTENCE));
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * If the files used to populate the index do not have unique identifiers
	 * this might lead to unwanted results. With this function one can choose
	 * whether the identifiers used in the files are also used to identify the
	 * documents in the index. If the id's are ignored, the indexer chooses an
	 * identifier for itself.
	 * @param ignoreId <code>true</code> is the identifiers in the files should
	 * be ignored, <code>false</code> if the original identifiers should be
	 * used
	 */
	public void setIgnoreId(boolean ignoreId) {
		this.ignoreId = ignoreId;
	}

	/**
	 * Set the type of input reader
	 * @param reader the input reader class
	 */
	public void setReader(Class<? extends Reader> reader)
	{
		readerClass = reader;
	}

	/**
	 * Set the type of index writer
	 * @param writer the index writer class
	 */
	public void setWriter(Class<?> writer)
	{
		writerClass = writer;
	}
}

/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

import nl.naiaden.twistinator.objects.Returnable;
import nl.naiaden.twistinator.objects.Searchable;

/**
 * @author louis
 *
 */
public class DocumentId implements Searchable, Returnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6106588427389405836L;
	private String documentId;

	public DocumentId(String documentId)
	{
		this.documentId = documentId;
	}

	/**
	 * @return the documentId
	 */
	public String getDocumentId()
	{
		return documentId;
	}

	@Override
	public String toString()
	{
		return documentId;
	}
}

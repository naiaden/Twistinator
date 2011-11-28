/**
 * 
 */
package nl.naiaden.twistinator.indexer.document;

import nl.naiaden.twistinator.objects.Returnable;


/**
 * @author louis
 *
 */

public class Triples implements Iterable<Triple>, Returnable
{
	/**
	 * This is the implementation of the ArrayListIterator.
	 * It maintains a notion of a current position and of
	 * course the implicit reference to the MyArrayList.
	 */
	private class ArrayListIterator implements java.util.Iterator<Triple>
	{
		private int current = 0;
		private boolean okToRemove = false;

		@Override
		public boolean hasNext( )
		{
			return current < size( );
		}


		@Override
		public Triple next( )
		{
			if( !hasNext( ) ) {
				throw new java.util.NoSuchElementException( );
			}

			okToRemove = true;
			return theItems[ current++ ];
		}

		@Override
		public void remove( )
		{
			if( !okToRemove ) {
				throw new IllegalStateException( );
			}

			Triples.this.remove( --current );
			okToRemove = false;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6456036091467844473L;

	private static final int DEFAULT_CAPACITY = 10;

	private Triple [ ] theItems;

	private int theSize;

	/**
	 * Construct an empty ArrayList.
	 */
	public Triples( )
	{
		clear( );
	}

	/**
	 * Adds an item to this collection, at the specified index.
	 * @param x any object.
	 * @return true.
	 */
	public void add( int idx, Triple x )
	{
		if( theItems.length == size( ) )
		{
			ensureCapacity( size( ) * 2 + 1 );
		}

		for( int i = theSize; i > idx; i-- )
		{
			theItems[ i ] = theItems[ i - 1 ];
		}

		theItems[ idx ] = x;
		theSize++;
	}

	/**
	 * Adds an item to this collection, at the end.
	 * @param x any object.
	 * @return true.
	 */
	public boolean add( Triple x )
	{
		add( size( ), x );
		return true;
	}

	/**
	 * Change the size of this collection to zero.
	 */
	public void clear( )
	{
		theSize = 0;
		ensureCapacity( DEFAULT_CAPACITY );
	}

	public void ensureCapacity( int newCapacity )
	{
		if( newCapacity < theSize ) {
			return;
		}

		Triple [ ] old = theItems;
		theItems = new Triple[ newCapacity ];
		for( int i = 0; i < size( ); i++ )
		{
			theItems[ i ] = old[ i ];
		}
	}

	/**
	 * Returns the item at position idx.
	 * @param idx the index to search in.
	 * @throws ArrayIndexOutOfBoundsException if index is out of range.
	 */
	public Triple get( int idx )
	{
		if( idx < 0 || idx >= size( ) ) {
			throw new ArrayIndexOutOfBoundsException( "Index " + idx + "; size " + size( ) );
		}
		return theItems[ idx ];
	}

	/**
	 * Returns true if this collection is empty.
	 * @return true if this collection is empty.
	 */
	public boolean isEmpty( )
	{
		return size( ) == 0;
	}

	/**
	 * Obtains an Iterator object used to traverse the collection.
	 * @return an iterator positioned prior to the first element.
	 */
	@Override
	public java.util.Iterator<Triple> iterator( )
	{
		return new ArrayListIterator( );
	}



	/**
	 * Removes an item from this collection.
	 * @param idx the index of the object.
	 * @return the item was removed from the collection.
	 */
	public Triple remove( int idx )
	{
		Triple removedItem = theItems[ idx ];

		for( int i = idx; i < size( ) - 1; i++ )
		{
			theItems[ i ] = theItems[ i + 1 ];
		}
		theSize--;

		return removedItem;
	}

	/**
	 * Changes the item at position idx.
	 * @param idx the index to change.
	 * @param newVal the new value.
	 * @return the old value.
	 * @throws ArrayIndexOutOfBoundsException if index is out of range.
	 */
	public Triple set( int idx, Triple newVal )
	{
		if( idx < 0 || idx >= size( ) ) {
			throw new ArrayIndexOutOfBoundsException( "Index " + idx + "; size " + size( ) );
		}
		Triple old = theItems[ idx ];
		theItems[ idx ] = newVal;

		return old;
	}

	/**
	 * Returns the number of items in this collection.
	 * @return the number of items in this collection.
	 */
	public int size( )
	{
		return theSize;
	}
	/**
	 * Returns a String representation of this collection.
	 */
	@Override
	public String toString( )
	{
		StringBuilder s = new StringBuilder("");

		boolean firstTriple = true;
		for (Triple t : theItems)
		{
			if (firstTriple)
			{
				if(t!=null) {s.append(t.toString()); }
				firstTriple = false;
			}
			/*
			 * Because Triples has a default capacity, which may be larger than the amount of
			 * triples belonging to a Document, there may be elements in Triples that are null
			 */
			else if(t != null)
			{
				s.append(" " + t.toString());
			}
		}

		return s.toString();
	}
}
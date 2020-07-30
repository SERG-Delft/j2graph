// code from cassandra
package org.apache.cassandra.utils.memory;

import java.nio.ByteBuffer;

import org.apache.cassandra.db.Clustering;
import org.apache.cassandra.db.Columns;
import org.apache.cassandra.db.rows.BTreeRow;
import org.apache.cassandra.db.rows.Cell;
import org.apache.cassandra.db.rows.Row;
import org.apache.cassandra.utils.ByteBufferUtil;

public abstract class Subclasses
{
    /**
     * Allocate a slice of the given length.
     */
    public ByteBuffer clone(ByteBuffer buffer)
    {
        assert buffer != null;
        if (buffer.remaining() == 0)
            return ByteBufferUtil.EMPTY_BYTE_BUFFER;
        ByteBuffer cloned = allocate(buffer.remaining());

        cloned.mark();
        cloned.put(buffer.duplicate());
        cloned.reset();
        return cloned;
    }

    public abstract ByteBuffer allocate(int size);

    public Row.Builder cloningBTreeRowBuilder()
    {
        return new CloningBTreeRowBuilder(this);
    }

    private static class CloningBTreeRowBuilder extends BTreeRow.Builder
    {
        private final AbstractAllocator allocator;

        private CloningBTreeRowBuilder(AbstractAllocator allocator)
        {
            super(true);
            this.allocator = allocator;
        }

        @Override
        public void newRow(Clustering clustering)
        {
            super.newRow(clustering.copy(allocator));
        }

        @Override
        public void addCell(Cell cell)
        {
            super.addCell(cell.copy(allocator));
        }
    }
}
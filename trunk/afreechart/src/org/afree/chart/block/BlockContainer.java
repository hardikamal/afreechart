/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -------------------
 * BlockContainer.java
 * -------------------
 * 
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 *
 * Original Author:  shiraki  (for Icom Systech Co., Ltd);
 * Contributor(s):   Sato Yoshiaki ;
 *                   Niwano Masayoshi;
 *
 * Changes (from 19-Nov-2010)
 * --------------------------
 * 19-Nov-2010 : port JFreeChart 1.0.13 to Android as "AFreeChart"
 * 14-Jan-2011 : Updated API docs
 * 
 * ------------- JFreeChart ---------------------------------------------
 * (C) Copyright 2004-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes:
 * --------
 * 22-Oct-2004 : Version 1 (DG);
 * 02-Feb-2005 : Added isEmpty() method (DG);
 * 04-Feb-2005 : Added equals(), clone() and implemented Serializable (DG);
 * 08-Feb-2005 : Updated for changes in RectangleConstraint (DG);
 * 20-Apr-2005 : Added new draw() method (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 20-Jul-2006 : Perform translation directly on drawing area, not via
 *               Graphics2D (DG);
 *
 */

package org.afree.chart.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.afree.ui.Size2D;
import org.afree.chart.entity.EntityCollection;
import org.afree.chart.entity.StandardEntityCollection;
import org.afree.graphics.geom.RectShape;
import android.graphics.Canvas;

/**
 * A container for a collection of {@link Block} objects. The container uses an
 * {@link Arrangement} object to handle the position of each block.
 */
public class BlockContainer extends AbstractBlock implements Block, Cloneable,
        Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 8199508075695195293L;

    /** The blocks within the container. */
    private List blocks;

    /** The object responsible for laying out the blocks. */
    private Arrangement arrangement;

    /**
     * Creates a new instance with default settings.
     */
    public BlockContainer() {
        this(new BorderArrangement());
    }

    /**
     * Creates a new instance with the specified arrangement.
     * 
     * @param arrangement
     *            the arrangement manager (<code>null</code> not permitted).
     */
    public BlockContainer(Arrangement arrangement) {
        if (arrangement == null) {
            throw new IllegalArgumentException("Null 'arrangement' argument.");
        }
        this.arrangement = arrangement;
        this.blocks = new ArrayList();
    }

    /**
     * Returns the arrangement (layout) manager for the container.
     * 
     * @return The arrangement manager (never <code>null</code>).
     */
    public Arrangement getArrangement() {
        return this.arrangement;
    }

    /**
     * Sets the arrangement (layout) manager.
     * 
     * @param arrangement
     *            the arrangement (<code>null</code> not permitted).
     */
    public void setArrangement(Arrangement arrangement) {
        if (arrangement == null) {
            throw new IllegalArgumentException("Null 'arrangement' argument.");
        }
        this.arrangement = arrangement;
    }

    /**
     * Returns <code>true</code> if there are no blocks in the container, and
     * <code>false</code> otherwise.
     * 
     * @return A boolean.
     */
    public boolean isEmpty() {
        return this.blocks.isEmpty();
    }

    /**
     * Returns an unmodifiable list of the {@link Block} objects managed by this
     * arrangement.
     * 
     * @return A list of blocks.
     */
    public List getBlocks() {
        return Collections.unmodifiableList(this.blocks);
    }

    /**
     * Adds a block to the container.
     * 
     * @param block
     *            the block (<code>null</code> permitted).
     */
    public void add(Block block) {
        add(block, null);
    }

    /**
     * Adds a block to the container.
     * 
     * @param block
     *            the block (<code>null</code> permitted).
     * @param key
     *            the key (<code>null</code> permitted).
     */
    public void add(Block block, Object key) {
        this.blocks.add(block);
        this.arrangement.add(block, key);
    }

    /**
     * Clears all the blocks from the container.
     */
    public void clear() {
        this.blocks.clear();
        this.arrangement.clear();
    }

    /**
     * Arranges the contents of the block, within the given constraints, and
     * returns the block size.
     * 
     * @param canvas
     *            the graphics device.
     * @param constraint
     *            the constraint (<code>null</code> not permitted).
     * 
     * @return The block size (in Canvas units, never <code>null</code>).
     */
    public Size2D arrange(Canvas canvas, RectangleConstraint constraint) {
        return this.arrangement.arrange(this, canvas, constraint);
    }

    /**
     * Draws the container and all the blocks within it.
     * 
     * @param canvas
     *            the graphics device.
     * @param area
     *            the area.
     */
    public void draw(Canvas canvas, RectShape area) {
        draw(canvas, area, null);
    }

    /**
     * Draws the block within the specified area.
     * 
     * @param canvas
     *            the graphics device.
     * @param area
     *            the area.
     * @param params
     *            passed on to blocks within the container (<code>null</code>
     *            permitted).
     * 
     * @return An instance of {@link EntityBlockResult}, or <code>null</code>.
     */
    public Object draw(Canvas canvas, RectShape area, Object params) {
        // check if we need to collect chart entities from the container
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams) params;
            if (ebp.getGenerateEntities()) {
                sec = new StandardEntityCollection();
            }
        }
        RectShape contentArea = (RectShape) area.clone();
        contentArea = trimMargin(contentArea);
        drawBorder(canvas, contentArea);
        contentArea = trimBorder(contentArea);
        contentArea = trimPadding(contentArea);
        Iterator iterator = this.blocks.iterator();
        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();
            RectShape bounds = block.getBounds();
            RectShape drawArea = new RectShape(bounds.getX()
                    + area.getX(), bounds.getY() + area.getY(), bounds
                    .getWidth(), bounds.getHeight());
            Object r = block.draw(canvas, drawArea, params);
            if (sec != null) {
                if (r instanceof EntityBlockResult) {
                    EntityBlockResult ebr = (EntityBlockResult) r;
                    EntityCollection ec = ebr.getEntityCollection();
                    sec.addAll(ec);
                }
            }
        }
        BlockResult result = null;
        if (sec != null) {
            result = new BlockResult();
            result.setEntityCollection(sec);
        }
        return result;
    }

    /**
     * Tests this container for equality with an arbitrary object.
     * 
     * @param obj
     *            the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BlockContainer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        BlockContainer that = (BlockContainer) obj;
        if (!this.arrangement.equals(that.arrangement)) {
            return false;
        }
        if (!this.blocks.equals(that.blocks)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the container.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException
     *             if there is a problem cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        BlockContainer clone = (BlockContainer) super.clone();
        // TODO : complete this
        return clone;
    }

}
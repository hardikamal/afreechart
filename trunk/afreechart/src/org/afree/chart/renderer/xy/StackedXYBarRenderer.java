/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *    AFreeChart: http://code.google.com/p/afreechart/
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -------------------------
 * StackedXYBarRenderer.java
 * -------------------------
 * (C) Copyright 2010, by Icom Systech Co., Ltd.
 * (C) Copyright 2004-2008, by Andreas Schroeder and Contributors.
 *
 * Original Author:  Andreas Schroeder;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Sato Yoshiaki (for Icom Systech Co., Ltd);
 *                   Niwano Masayoshi;
 *
 * Changes
 * -------
 * 01-Apr-2004 : Version 1 (AS);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with
 *               getYValue() (DG);
 * 15-Aug-2004 : Added drawBarOutline to control draw/don't-draw bar
 *               outlines (BN);
 * 10-Sep-2004 : drawBarOutline attribute is now inherited from XYBarRenderer
 *               and double primitives are retrieved from the dataset rather
 *               than Number objects (DG);
 * 07-Jan-2005 : Updated for method name change in DatasetUtilities (DG);
 * 25-Jan-2005 : Modified to handle negative values correctly (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 06-Dec-2006 : Added support for GradientPaint (DG);
 * 15-Mar-2007 : Added renderAsPercentages option (DG);
 * 24-Jun-2008 : Added new barPainter mechanism (DG);
 * 23-Sep-2008 : Check shadow visibility before drawing shadow (DG);
 *
 * ------------- AFREECHART 0.0.1 ---------------------------------------------
 * 19-Nov-2010 : port JFreeChart 1.0.13 to Android as "AFreeChart"
 */

package org.afree.chart.renderer.xy;

import org.afree.ui.RectangleEdge;
import org.afree.ui.TextAnchor;
import org.afree.chart.axis.ValueAxis;
import org.afree.data.xy.IntervalXYDataset;
import org.afree.data.Range;
import org.afree.data.xy.XYDataset;
import org.afree.data.general.DatasetUtilities;
import org.afree.data.xy.TableXYDataset;
import org.afree.chart.entity.EntityCollection;
import org.afree.chart.event.RendererChangeEvent;
import org.afree.chart.labels.ItemLabelAnchor;
import org.afree.chart.labels.ItemLabelPosition;
import org.afree.chart.labels.XYItemLabelGenerator;
import org.afree.chart.plot.CrosshairState;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.PlotRenderingInfo;
import org.afree.chart.plot.XYPlot;
import org.afree.graphics.geom.RectShape;
import android.graphics.Canvas;

/**
 * A bar renderer that displays the series items stacked.
 * The dataset used together with this renderer must be a
 * {@link org.afree.data.xy.IntervalXYDataset} and a
 * {@link org.afree.data.xy.TableXYDataset}. For example, the
 * dataset class {@link org.afree.data.xy.CategoryTableXYDataset}
 * implements both interfaces.
 *
 * The example shown here is generated by the
 * <code>StackedXYBarChartDemo2.java</code> program included in the
 * AFreeChart demo collection:
 * <br><br>
 * <img src="../../../../../images/StackedXYBarRendererSample.png"
 * alt="StackedXYBarRendererSample.png" />

 */
public class StackedXYBarRenderer extends XYBarRenderer {

    /** For serialization. */
    private static final long serialVersionUID = -7049101055533436444L;

    /** A flag that controls whether the bars display values or percentages. */
    private boolean renderAsPercentages;

    /**
     * Creates a new renderer.
     */
    public StackedXYBarRenderer() {
        this(0.0);
    }

    /**
     * Creates a new renderer.
     *
     * @param margin  the percentual amount of the bars that are cut away.
     */
    public StackedXYBarRenderer(double margin) {
        super(margin);
        this.renderAsPercentages = false;

        // set the default item label positions, which will only be used if
        // the user requests visible item labels...
        ItemLabelPosition p = new ItemLabelPosition(ItemLabelAnchor.CENTER,
                TextAnchor.CENTER);
        setBasePositiveItemLabelPosition(p);
        setBaseNegativeItemLabelPosition(p);
        setPositiveItemLabelPositionFallback(null);
        setNegativeItemLabelPositionFallback(null);
    }

    /**
     * Returns <code>true</code> if the renderer displays each item value as
     * a percentage (so that the stacked bars add to 100%), and
     * <code>false</code> otherwise.
     *
     * @return A boolean.
     *
     * @see #setRenderAsPercentages(boolean)
     *
     * @since JFreeChart 1.0.5
     */
    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;
    }

    /**
     * Sets the flag that controls whether the renderer displays each item
     * value as a percentage (so that the stacked bars add to 100%), and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param asPercentages  the flag.
     *
     * @see #getRenderAsPercentages()
     *
     * @since JFreeChart 1.0.5
     */
    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages;
        //fireChangeEvent();
    }

    /**
     * Returns <code>3</code> to indicate that this renderer requires three
     * passes for drawing (shadows are drawn in the first pass, the bars in the
     * second, and item labels are drawn in the third pass so that
     * they always appear in front of all the bars).
     *
     * @return <code>2</code>.
     */
    public int getPassCount() {
        return 3;
    }

    /**
     * Initialises the renderer and returns a state object that should be
     * passed to all subsequent calls to the drawItem() method. Here there is
     * nothing to do.
     *
     * @param canvas  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return A state object.
     */
    public XYItemRendererState initialise(Canvas canvas,
                                          RectShape dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {
        return new XYBarRendererState(info);
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The range (<code>null</code> if the dataset is <code>null</code>
     *         or empty).
     */
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            if (this.renderAsPercentages) {
                return new Range(0.0, 1.0);
            }
            else {
                return DatasetUtilities.findStackedRangeBounds(
                        (TableXYDataset) dataset);
            }
        }
        else {
            return null;
        }
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param canvas  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Canvas canvas,
                         XYItemRendererState state,
                         RectShape dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        if (!(dataset instanceof IntervalXYDataset
                && dataset instanceof TableXYDataset)) {
            String message = "dataset (type " + dataset.getClass().getName()
                + ") has wrong type:";
            boolean and = false;
            if (!IntervalXYDataset.class.isAssignableFrom(dataset.getClass())) {
                message += " it is no IntervalXYDataset";
                and = true;
            }
            if (!TableXYDataset.class.isAssignableFrom(dataset.getClass())) {
                if (and) {
                    message += " and";
                }
                message += " it is no TableXYDataset";
            }

            throw new IllegalArgumentException(message);
        }

        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
        double value = intervalDataset.getYValue(series, item);
        if (Double.isNaN(value)) {
            return;
        }

        // if we are rendering the values as percentages, we need to calculate
        // the total for the current item.  Unfortunately here we end up
        // repeating the calculation more times than is strictly necessary -
        // hopefully I'll come back to this and find a way to add the
        // total(s) to the renderer state.  The other problem is we implicitly
        // assume the dataset has no negative values...perhaps that can be
        // fixed too.
        double total = 0.0;
        if (this.renderAsPercentages) {
            total = DatasetUtilities.calculateStackTotal(
                    (TableXYDataset) dataset, item);
            value = value / total;
        }

        double positiveBase = 0.0;
        double negativeBase = 0.0;

        for (int i = 0; i < series; i++) {
            double v = dataset.getYValue(i, item);
            if (!Double.isNaN(v)) {
                if (this.renderAsPercentages) {
                    v = v / total;
                }
                if (v > 0) {
                    positiveBase = positiveBase + v;
                }
                else {
                    negativeBase = negativeBase + v;
                }
            }
        }

        double translatedBase;
        double translatedValue;
        RectangleEdge edgeR = plot.getRangeAxisEdge();
        if (value > 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea,
                    edgeR);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value,
                    dataArea, edgeR);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea,
                    edgeR);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value,
                    dataArea, edgeR);
        }

        RectangleEdge edgeD = plot.getDomainAxisEdge();
        double startX = intervalDataset.getStartXValue(series, item);
        if (Double.isNaN(startX)) {
            return;
        }
        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea,
                edgeD);

        double endX = intervalDataset.getEndXValue(series, item);
        if (Double.isNaN(endX)) {
            return;
        }
        double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, edgeD);

        double translatedWidth = Math.max(1, Math.abs(translatedEndX
                - translatedStartX));
        double translatedHeight = Math.abs(translatedValue - translatedBase);
        if (getMargin() > 0.0) {
            double cut = translatedWidth * getMargin();
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut / 2;
        }

        RectShape bar = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new RectShape(Math.min(translatedBase,
                    translatedValue), translatedEndX, translatedHeight,
                    translatedWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new RectShape(translatedStartX,
                    Math.min(translatedBase, translatedValue),
                    translatedWidth, translatedHeight);
        }
        boolean positive = (value > 0.0);
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase;
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (positive && inverted || !positive && !inverted) {
                barBase = RectangleEdge.RIGHT;
            }
            else {
                barBase = RectangleEdge.LEFT;
            }
        }
        else {
            if (positive && !inverted || !positive && inverted) {
                barBase = RectangleEdge.BOTTOM;
            }
            else {
                barBase = RectangleEdge.TOP;
            }
        }

        if (pass == 0) {
            if (getShadowsVisible()) {
                getBarPainter().paintBarShadow(canvas, this, series, item, bar,
                        barBase, false);
            }
        }
        else if (pass == 1) {
            getBarPainter().paintBar(canvas, this, series, item, bar, barBase);

            // add an entity for the item...
            if (info != null) {
                EntityCollection entities = info.getOwner()
                        .getEntityCollection();
                if (entities != null) {
                    addEntity(entities, bar, dataset, series, item,
                            bar.getCenterX(), bar.getCenterY());
                }
            }
        }
        else if (pass == 2) {
            // handle item label drawing, now that we know all the bars have
            // been drawn...
            if (isItemLabelVisible(series, item)) {
                XYItemLabelGenerator generator = getItemLabelGenerator(series,
                        item);
                drawItemLabel(canvas, dataset, series, item, plot, generator, bar,
                        value < 0.0);
            }
        }

    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StackedXYBarRenderer)) {
            return false;
        }
        StackedXYBarRenderer that = (StackedXYBarRenderer) obj;
        if (this.renderAsPercentages != that.renderAsPercentages) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    public int hashCode() {
        int result = super.hashCode();
        result = result * 37 + (this.renderAsPercentages ? 1 : 0);
        return result;
    }

}
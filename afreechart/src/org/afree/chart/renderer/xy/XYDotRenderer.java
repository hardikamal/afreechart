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
 * ------------------
 * XYDotRenderer.java
 * ------------------
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
 * 
 * ------------- JFreeChart ---------------------------------------------
 * (C) Copyright 2002-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * Changes (from 29-Oct-2002)
 * --------------------------
 * 29-Oct-2002 : Added standard header (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 19-Jan-2005 : Now uses only primitives from dataset (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 10-Jul-2006 : Added dotWidth and dotHeight attributes (DG);
 * 06-Feb-2007 : Fixed bug 1086307, crosshairs with multiple axes (DG);
 * 09-Nov-2007 : Added legend shape attribute, plus override for
 *               getLegendItem() (DG);
 * 17-Jun-2008 : Apply legend shape, font and paint attributes (DG);
 *
 */

package org.afree.chart.renderer.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import org.afree.util.PublicCloneable;
import org.afree.ui.RectangleEdge;
import org.afree.util.ShapeUtilities;
import org.afree.chart.LegendItem;
import org.afree.chart.axis.ValueAxis;
import org.afree.data.xy.XYDataset;
import org.afree.chart.plot.CrosshairState;
import org.afree.chart.plot.PlotOrientation;
import org.afree.chart.plot.PlotRenderingInfo;
import org.afree.chart.plot.XYPlot;
import org.afree.graphics.geom.RectShape;
import org.afree.graphics.geom.Shape;
import org.afree.graphics.PaintType;
import org.afree.graphics.PaintUtility;



/**
 * A renderer that draws a small dot at each data point for an {@link XYPlot}.
 * The example shown here is generated by the
 * <code>ScatterPlotDemo4.java</code> program included in the AFreeChart
 * demo collection:
 * <br><br>
 * <img src="../../../../../images/XYDotRendererSample.png"
 * alt="XYDotRendererSample.png" />
 */
public class XYDotRenderer extends AbstractXYItemRenderer
        implements XYItemRenderer, PublicCloneable {

    /** For serialization. */
    private static final long serialVersionUID = -2764344339073566425L;

    /** The dot width. */
    private int dotWidth;

    /** The dot height. */
    private int dotHeight;

    /**
     * The shape that is used to represent an item in the legend.
     *
     * @since JFreeChart 1.0.7
     */
    private transient Shape legendShape;

    /**
     * Constructs a new renderer.
     */
    public XYDotRenderer() {
        super();
        this.dotWidth = 1;
        this.dotHeight = 1;
        this.legendShape = new RectShape(-3.0, -3.0, 6.0, 6.0);
    }

    /**
     * Returns the dot width (the default value is 1).
     *
     * @return The dot width.
     *
     * @since JFreeChart 1.0.2
     * @see #setDotWidth(int)
     */
    public int getDotWidth() {
        return this.dotWidth;
    }

    /**
     * Sets the dot width and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param w  the new width (must be greater than zero).
     *
     * @throws IllegalArgumentException if <code>w</code> is less than one.
     *
     * @since JFreeChart 1.0.2
     * @see #getDotWidth()
     */
    public void setDotWidth(int w) {
        if (w < 1) {
            throw new IllegalArgumentException("Requires w > 0.");
        }
        this.dotWidth = w;
        fireChangeEvent();
    }

    /**
     * Returns the dot height (the default value is 1).
     *
     * @return The dot height.
     *
     * @since JFreeChart 1.0.2
     * @see #setDotHeight(int)
     */
    public int getDotHeight() {
        return this.dotHeight;
    }

    /**
     * Sets the dot height and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param h  the new height (must be greater than zero).
     *
     * @throws IllegalArgumentException if <code>h</code> is less than one.
     *
     * @since JFreeChart 1.0.2
     * @see #getDotHeight()
     */
    public void setDotHeight(int h) {
        if (h < 1) {
            throw new IllegalArgumentException("Requires h > 0.");
        }
        this.dotHeight = h;
        fireChangeEvent();
    }

    /**
     * Returns the shape used to represent an item in the legend.
     *
     * @return The legend shape (never <code>null</code>).
     *
     * @see #setLegendShape(Shape)
     *
     * @since JFreeChart 1.0.7
     */
    public Shape getLegendShape() {
        return this.legendShape;
    }

    /**
     * Sets the shape used as a line in each legend item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shape  the shape (<code>null</code> not permitted).
     *
     * @see #getLegendShape()
     *
     * @since JFreeChart 1.0.7
     */
    public void setLegendShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendShape = shape;
        fireChangeEvent();
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param canvas  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
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

        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }

        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double adjx = (this.dotWidth - 1) / 2.0;
        double adjy = (this.dotHeight - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea,
                    xAxisLocation) - adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation)
                    - adjy;

//            canvas.setPaint(getItemPaint(series, item));
            Paint paint = PaintUtility.createPaint(
                    Paint.ANTI_ALIAS_FLAG,
                    getItemPaintType(series, item));
            paint.setStyle(Style.FILL);
            PlotOrientation orientation = plot.getOrientation();
            /*
            if (orientation == PlotOrientation.HORIZONTAL) {
                canvas.fillRect((int) transY, (int) transX, this.dotHeight,
                        this.dotWidth);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                canvas.fillRect((int) transX, (int) transY, this.dotWidth,
                        this.dotHeight);
            }*/
            if (orientation == PlotOrientation.HORIZONTAL) {
                canvas.drawRect((int) transX,(int) transY + this.dotHeight, (int)  transX + this.dotWidth,
                        (int) transY ,paint);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                canvas.drawRect((int) transX,(int) transY + this.dotHeight, (int)  transX + this.dotWidth,
                        (int) transY ,paint);
            }
            

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
                    rangeAxisIndex, transX, transY, orientation);
        }

    }

    /**
     * Returns a legend item for the specified series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series (possibly <code>null</code>).
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        // if the renderer isn't assigned to a plot, then we don't have a
        // dataset...
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }

        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }

        LegendItem result = null;
        if (getItemVisible(series, 0)) {
            String label = getLegendItemLabelGenerator().generateLabel(dataset,
                    series);
            String description = label;
            String toolTipText = null;
           /*
            if (getLegendItemToolTipGenerator() != null) {
                toolTipText = getLegendItemToolTipGenerator().generateLabel(
                        dataset, series);
            }
            */
            String urlText = null;
            if (getLegendItemURLGenerator() != null) {
                urlText = getLegendItemURLGenerator().generateLabel(
                        dataset, series);
            }
            PaintType fillPaintType = lookupSeriesPaintType(series);           
           
            result = new LegendItem(label, description, toolTipText, urlText,
                    getLegendShape(), fillPaintType);
            result.setLabelFont(lookupLegendTextFont(series));
            PaintType labelPaintType = lookupLegendTextPaintType(series);
            if (labelPaintType != null) {
                result.setLabelPaintType(labelPaintType);
            }
            result.setSeriesKey(dataset.getSeriesKey(series));
            result.setSeriesIndex(series);
            result.setDataset(dataset);
            result.setDatasetIndex(datasetIndex);
        }

        return result;

    }

    /**
     * Tests this renderer for equality with an arbitrary object.  This method
     * returns <code>true</code> if and only if:
     *
     * <ul>
     * <li><code>obj</code> is not <code>null</code>;</li>
     * <li><code>obj</code> is an instance of <code>XYDotRenderer</code>;</li>
     * <li>both renderers have the same attribute values.
     * </ul>
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDotRenderer)) {
            return false;
        }
        XYDotRenderer that = (XYDotRenderer) obj;
        if (this.dotWidth != that.dotWidth) {
            return false;
        }
        if (this.dotHeight != that.dotHeight) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendShape, that.legendShape)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    /*
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendShape = SerialUtilities.readShape(stream);
    }*/

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    /*
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendShape, stream);
    }*/

}

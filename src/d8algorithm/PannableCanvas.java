package d8algorithm;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class PannableCanvas extends Pane {

    DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    public PannableCanvas() {
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }

    public void drawFlowDirection(int[][] flowDirection) {
        if (flowDirection != null) {
            double cellSize = 40;//lebar cell
            double arrowSize = 5;//panjang sisi arrow
            double h = cellSize * flowDirection.length;
            double w = cellSize * flowDirection[0].length;
            Canvas grid = new Canvas(w, h);

            //don't catch mouse events
            grid.setMouseTransparent(true);

            GraphicsContext gc = grid.getGraphicsContext2D();

            for (int i = 0; i < flowDirection.length; i++) {
                for (int j = 0; j < flowDirection[i].length; j++) {
                    int arah = flowDirection[i][j];
                    if (arah >= 0 && arah <= 8) {
                        Color c = Color.valueOf("#f1c40f");
                        gc.setFill(c);
                        gc.setStroke(c);
                        double xo = cellSize * j;
                        double yo = cellSize * i;
                        gc.fillRect(xo, yo, cellSize, cellSize);
                        drawArrow(gc, xo, yo, arah, cellSize, arrowSize);
                    }
                }
            }
            getChildren().add(grid);
            grid.toBack();
        }
    }//END OF drawFlowDirection-------------------------------------------------

    private void drawArrow(GraphicsContext gc, double xo, double yo, int arah, double cellSize, double arrowSize) {
        double r = cellSize / 2.0;
        double d = Math.sqrt(Math.pow(r, 2) / 2.0) - 1;
        double d2 = d / 2.0;//setengah dari d
        double xc = xo + r;
        double yc = yo + r;

        //arrow
        double c = arrowSize;
        double a = c / 2.0;
        double b = Math.sqrt(Math.pow(c, 2) - Math.pow(a, 2));
        double[] xPoints = new double[]{d, d - b, d - b};
        double[] yPoints = new double[]{0, a, -a};
        int nPoints = 3;

        //set sudut rotasi
        double sudutRotasi = -90;
        if (arah == 0) {
            sudutRotasi = 0;
        } else if (arah == 1) {
            sudutRotasi = -90;
        }else if (arah == 2) {
            sudutRotasi = -45;
        }else if (arah == 3) {
            sudutRotasi = 0;
        }else if (arah == 4) {
            sudutRotasi = 45;
        }else if (arah == 5) {
            sudutRotasi = 90;
        }else if (arah == 6) {
            sudutRotasi = 135;
        }else if (arah == 7) {
            sudutRotasi = 180;
        }else if (arah == 8) {
            sudutRotasi = -135;
        }
        
        //MENGGAMBAR PANAH
        gc.save();
        Transform transform = Transform.translate(xc, yc);
        transform = transform.createConcatenation(Transform.rotate(sudutRotasi, 0, 0));
        gc.setTransform(new Affine(transform));
        gc.setStroke(Color.BLACK);//Warna Garis
        gc.setFill(Color.BLACK);//Warna Panah
        if(arah==0){
            gc.strokeOval(-d2, -d2, d, d);
        }else{
            gc.strokeLine(-d, 0, d, 0);
            gc.fillPolygon(xPoints, yPoints, nPoints);
        }            
        gc.restore();
    }//END OF drawArrow---------------------------------------------------------

    public double getScale() {
        return myScale.get();
    }

    public void setScale(double scale) {
        this.myScale.set(scale);
    }
    
    public void setPivot(double x, double y){
        setTranslateX(getTranslateX()-x);
        setTranslateY(getTranslateY()-y);
    }
}



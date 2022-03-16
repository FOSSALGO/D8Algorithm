/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package d8algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author VD1
 */
public class UID8Algorithm extends Application {

    //MY COMPONENTS AND VARIABLES
    TextArea textArea01;
    TextArea textArea02;
    PannableCanvas canvas;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        GridPane gridPane01 = new GridPane();
        gridPane01.setHgap(8);
        gridPane01.setVgap(8);
        gridPane01.setPadding(new Insets(10));

        ColumnConstraints cConst1 = new ColumnConstraints();
        cConst1.setHgrow(Priority.NEVER);
        ColumnConstraints cConst2 = new ColumnConstraints();
        cConst2.setHgrow(Priority.ALWAYS);
        gridPane01.getColumnConstraints().addAll(cConst1, cConst2, cConst1, cConst1);

        Label label01 = new Label("File Data");
        TextField textField01 = new TextField();
        Button button01 = new Button("Browse");
        Button button02 = new Button("Run D8");

        //tambahkan komponen ke grid pane
        gridPane01.add(label01, 0, 0);
        gridPane01.add(textField01, 1, 0, 3, 1);
        gridPane01.add(button01, 4, 0);
        gridPane01.add(button02, 5, 0);

        //tambahkan grid pane ke root
        root.setTop(gridPane01);

        //AKSI TOMBOL
        button01.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                if(selectedFile != null){
                    textField01.setText(selectedFile.getAbsolutePath());
                    bacaData(selectedFile);
                    System.out.println("File Selected: "+selectedFile.toString());
                }else{
                    System.out.println("Open Command Canceled");
                }
            }

        });
        
        button02.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pencarianFlowDirection();
            }

        });

        ///UNTUK ACTION BUTTON
        //MEMBUAT TAB PANE
        TabPane tabPane01 = new TabPane();
        tabPane01.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane01.setSide(Side.BOTTOM);

        //Tab 1-------------------------------
        Tab tab01 = new Tab();
        tab01.setText("Data");
        textArea01 = new TextArea();
        tab01.setContent(textArea01);

        //Tab 2-------------------------------
        Tab tab02 = new Tab();
        tab02.setText("Flow Direction");
        textArea02 = new TextArea();
        tab02.setContent(textArea02);

        //Tab 3-------------------------------
        Tab tab03 = new Tab();
        tab03.setText("Visualisasi");
        //create canvas
        canvas = new PannableCanvas();
        //set lokasi canvas ke sudut kiri-atas tab03
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);
        tab03.setContent(canvas);

        //set elemen tabPane01
        tabPane01.getTabs().addAll(tab01, tab02, tab03);
        root.setCenter(tabPane01);

        //SET SCENE        
        Scene scene = new Scene(root, 1000, 700);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        primaryStage.setTitle("Water Flow Direction");
        primaryStage.setScene(scene);
        primaryStage.show();
    }//END OF START

    //==========================================================================
    //MY METHODS AND VARIABLES
    private int ncols;
    private int nrows;
    private double[][] data = null;
    private int[][] flowDirection = null;

    private void bacaData(File file) {
        textArea01.setText("");
        textArea02.setText("");
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String baris = br.readLine();//membaca baris pertama
            baris = baris.replaceAll("\\s+", ";");
            String[] splitBaris = baris.split(";");
            ncols = Integer.parseInt(splitBaris[1]);

            baris = br.readLine();//membaca baris pertama
            baris = baris.replaceAll("\\s+", ";");
            splitBaris = baris.split(";");
            nrows = Integer.parseInt(splitBaris[1]);

            data = new double[nrows][];

            //melompat empat baris
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();

            //MEMBACA ELEMEN DATA BARIS DEMI BARIS
            for (int i = 0; i < nrows; i++) {
                baris = br.readLine();
                baris = baris.replaceAll("\\s+", ";");
                splitBaris = baris.split(";");

                data[i] = new double[splitBaris.length];
                for (int j = 0; j < data[i].length; j++) {
                    String sValue = splitBaris[j].trim();
                    double dValue = Double.parseDouble(sValue);
                    data[i][j] = dValue;
                }
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("GAGAL MEMBACA FILE DATA");
            e.printStackTrace();
        }
        //tampilkan data ke textArea01
        if (data != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    sb.append(data[i][j] + "\t");
                }
                sb.append("\n");
            }
            textArea01.setText(sb.toString());
        }
    }

    private void pencarianFlowDirection() {
        textArea02.setText("");
        if (data != null) {
            //PROSES PENCARIAN
            //SINGLE FLOW DIRECTION D8
            flowDirection = new int[data.length][data[0].length];
            for (int i = 0; i < flowDirection.length; i++) {
                for (int j = 0; j < flowDirection[i].length; j++) {
                    //+--+--+--+
                    //|D8|D1|D2|
                    //+--+--+--+
                    //|D7|D0|D3|
                    //+--+--+--+
                    //|D6|D5|D5|
                    //+--+--+--+

                    int arah = 0;
                    double MIN = Double.MAX_VALUE;

                    //D1
                    int I = i - 1;
                    int J = j;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 1;
                    }

                    //D2
                    I = i - 1;
                    J = j + 1;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 2;
                    }

                    //D3
                    I = i;
                    J = j + 1;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 3;
                    }

                    //D4
                    I = i + 1;
                    J = j + 1;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 4;
                    }

                    //D5
                    I = i + 1;
                    J = j;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 5;
                    }

                    //D6
                    I = i + 1;
                    J = j - 1;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 6;
                    }

                    //D7
                    I = i;
                    J = j - 1;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) {
                        MIN = data[I][J];
                        arah = 7;
                    }

                    //D8
                    I = i - 1;
                    J = j - 1;
                    if (I >= 0 && I < data.length && J >+ 0 && J < data[i].length && data[I][J] < MIN) { 
                        MIN = data[I][J];
                        arah = 8;
                    }

                    //SET ARAH ALIRAN
                    flowDirection[i][j] = 0;
                    if (MIN <= data[i][j]) {
                        flowDirection[i][j] = arah;
                    }

                }
            }//END OF FOR i

            //COBA CETAK FLOW DIRECTION
            if (flowDirection != null) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < flowDirection.length; i++) {
                    for (int j = 0; j < flowDirection[i].length; j++) {
                        sb.append(flowDirection[i][j] + "\t");
                    }
                    sb.append("\n");
                }
                textArea02.setText(sb.toString());
                canvas.drawFlowDirection(flowDirection);
            }

        }
    }

    //==========================================================================
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

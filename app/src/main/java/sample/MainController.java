package sample;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.svg.SVGGlyphLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.listener.ParseListener;
import sample.model.ColumnInfo;
import sample.model.LocationInfo;
import sample.model.MatchResult;
import sample.util.ParseUtil;

@ViewController(value = "/sample.fxml", title = "Material Design Example")
public class MainController implements Initializable {

    public static final String CONTENT_PANE = "ContentPane";

    @FXMLViewFlowContext
    private ViewFlowContext context;

    private String RESULT_FILENAME = "最终结果.xlsx";

    // file A
    @FXML
    private TextField mTF_sheetIndexA;
    @FXML
    private TextField mTF_columnIndexAName;
    @FXML
    private TextField mTF_columnIndexALong;
    @FXML
    private TextField mTF_columnIndexALati;
    @FXML
    private TextArea mTextAreaA;
    private List<Object> mDataA;
    private List<LocationInfo> mDataListA = new ArrayList<>();
    private List<Object> mHeader;

    // file B
    @FXML
    private TextField mTF_sheetIndexB;
    @FXML
    private TextField mTF_columnIndexBName;
    @FXML
    private TextField mTF_columnIndexBLong;
    @FXML
    private TextField mTF_columnIndexBLati;
    @FXML
    private TextArea mTextAreaB;
    private List<Object> mDataB;
    private List<LocationInfo> mDataListB = new ArrayList<>();

    @FXML
    private TextField mTF_matchDistance;
    @FXML
    private JFXButton doBtn;
    @FXML
    private JFXDialog dialog;

    private JFXAlert alert;

    private int mParseCompletedCount = 0;

    private OnDragOverEvent mOnDragOverEvent = new OnDragOverEvent();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mTextAreaA.setOnDragOver(mOnDragOverEvent);
        mTextAreaA.setOnDragDropped(new OnDragDroppedEvent(mTextAreaA));
        mTextAreaB.setOnDragOver(mOnDragOverEvent);
        mTextAreaB.setOnDragDropped(new OnDragDroppedEvent(mTextAreaB));

    }

    private class OnDragOverEvent implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            if (event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        }
    }

    private class OnDragDroppedEvent implements EventHandler<DragEvent> {

        private TextArea textArea;

        public OnDragDroppedEvent(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void handle(DragEvent event) {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                textArea.setText(db.getFiles().toString().replace("[", "").replace("]", ""));

                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();
        }
    }

    @FXML
    private void handleClickDoAction() {
        System.out.println("点击");

        startDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String fileNameA = mTextAreaA.getText();
                    String fileNameB = mTextAreaB.getText();

                    int sheetA = Integer.valueOf(mTF_sheetIndexA.getText());
                    int sheetB = Integer.valueOf(mTF_sheetIndexB.getText());

                    if ((fileNameA.endsWith(".xls") || fileNameA.endsWith(".xlsx")) &&
                            (fileNameB.endsWith(".xls") || fileNameB.endsWith(".xlsx"))) {

                        int columnAName = Integer.valueOf(mTF_columnIndexAName.getText());
                        int columnALong = Integer.valueOf(mTF_columnIndexALong.getText());
                        int columnALati = Integer.valueOf(mTF_columnIndexALati.getText());
                        int columnBName = Integer.valueOf(mTF_columnIndexBName.getText());
                        int columnBLong = Integer.valueOf(mTF_columnIndexBLong.getText());
                        int columnBLati = Integer.valueOf(mTF_columnIndexBLati.getText());

                        ParseUtil.parseSheetInExcel(fileNameA, sheetA, new ParseListenerImp(mTextAreaA.getId(), new
                                ColumnInfo(columnAName, columnALong, columnALati)));
                        ParseUtil.parseSheetInExcel(fileNameB, sheetB, new ParseListenerImp(mTextAreaB.getId(), new
                                ColumnInfo(columnBName, columnBLong, columnBLati)));

                    } else {
                        System.out.println("文件格式错误");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void startDialog() {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Spinner.fxml"));

            alert = new JFXAlert((Stage) doBtn.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("玩命干活中......"));

            layout.setBody(root);
            alert.setContent(layout);
            alert.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopDialog() {
        if (alert != null && alert.isShowing()) {
            alert.hide();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        alert = new JFXAlert((Stage) doBtn.getScene().getWindow());
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.setOverlayClose(true);
                        JFXDialogLayout layout = new JFXDialogLayout();
                        layout.setHeading(new Label("哈哈哈，工作做完了，玩去吧^-^"));

                        // HBox
                        HBox hb = new HBox();
                        hb.setPadding(new Insets(15, 12, 15, 12));
                        hb.setSpacing(10);
                        hb.setAlignment(Pos.CENTER);

                        String[] filenames = new String[]{"1.png", "2.png", "3.png", "4.png", "5.png"};
                        ImageView[] imageViews = new ImageView[5];
                        for (int i = 0; i < filenames.length; i++) {
                            ImageView imageView = new ImageView(new Image(filenames[i]));
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            imageViews[i] = imageView;
                            hb.getChildren().add(imageViews[i]);
                        }

                        layout.setBody(hb);

                        alert.setContent(layout);
                        alert.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public class ParseListenerImp implements ParseListener {

        private String uniqueID = "";
        private ColumnInfo columnInfo;

        public ParseListenerImp(String uniqueID, ColumnInfo columnInfo) {
            this.uniqueID = uniqueID;
            this.columnInfo = columnInfo;
        }

        @Override
        public void onParseCompleted(List<Object> data) {

            try {
                System.out.println("aaaa");
                if ("mTextAreaA".equals(uniqueID)) {

                    mHeader = (List<Object>) data.get(0);
                    data.remove(0);
                    mDataA = data;
                    selectColumn(data, mDataListA, columnInfo);

                } else if ("mTextAreaB".equals(uniqueID)) {

                    data.remove(0);
                    mDataB = data;
                    selectColumn(data, mDataListB, columnInfo);
                }

                mParseCompletedCount++;
                // 两个文件解析完成
                if (mParseCompletedCount == 2) {
                    mParseCompletedCount = 0;

                    float matchDistance = Float.valueOf(mTF_matchDistance.getText());
                    List<List<MatchResult>> result = ParseUtil.beginMatch(mDataListA, mDataListB,
                            matchDistance);

                    // 将匹配出的result的站点名称，增加至原始数据中
                    ParseUtil.convertMatchResult(mDataA, result);

                    List<List<String>> headersList = new ArrayList<>();
                    List<String> headers = new ArrayList<>();
                    for (Object o : mHeader) {
                        headers.add(o.toString());
                    }
                    headersList.add(headers);
                    List header = headersList;

                    int sheetA = Integer.valueOf(mTF_sheetIndexA.getText());
                    mDataA.add(0, mHeader);
                    List aa = mDataA;
                    String fileNameA = mTextAreaA.getText();
                    ParseUtil.writeResult(getResultFileName(fileNameA), sheetA, header, (List<List<Object>>) aa);

                    stopDialog();
                    System.out.println("111");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getResultFileName(String filename) {

            int index = Math.max(filename.lastIndexOf("/"), filename.lastIndexOf("\\"));
            return filename.substring(0, index + 1) + RESULT_FILENAME;
        }

        private void selectColumn(List<Object> data, List<LocationInfo> selectedList, ColumnInfo columnInfo) {

            selectedList.clear();
            for (int i = 0; i < data.size(); i++) {

                try {

                    Object item = data.get(i);
                    String name = ((List<String>)item).get(columnInfo.columnName - 1);
                    double longitude = Double.valueOf(((List<String>)item).get(columnInfo.columnLong - 1));
                    double latitude = Double.valueOf(((List<String>)item).get(columnInfo.columnLati - 1));
                    LocationInfo locationInfo = new LocationInfo(i, name, longitude, latitude);
                    selectedList.add(locationInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

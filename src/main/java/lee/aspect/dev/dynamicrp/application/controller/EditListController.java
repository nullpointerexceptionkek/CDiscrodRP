/*
 * 2022-
 * MIT License
 *
 * Copyright (c) 2023 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.dynamicrp.application.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lee.aspect.dev.dynamicrp.animatefx.Shake;
import lee.aspect.dev.dynamicrp.application.core.Script;
import lee.aspect.dev.dynamicrp.application.core.Updates;
import lee.aspect.dev.dynamicrp.manager.SceneManager;
import lee.aspect.dev.dynamicrp.util.WarningManager;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditListController implements Initializable {
    private Stage stage;
    @FXML
    private TextField Wait;
    @FXML
    private TextField image;
    @FXML
    private TextField imagetext;
    @FXML
    private TextField smallimage;
    @FXML
    private TextField smalltext;
    @FXML
    private TextField firstline;
    @FXML
    private TextField secondline;
    @FXML
    private TextField button1Text;
    @FXML
    private TextField button1Url;
    @FXML
    private TextField button2Text;
    @FXML
    private TextField button2Url;
    @FXML
    private Button CancelButton;
    @FXML
    private Button SaveButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private VBox content;

    @FXML
    private Label EditConfiLabel, FirstLineLabel, SecondLineLabel, DelayLabel, LargeImgLabel, SmallImgLabel,
            SmallImgTxtLabel, LargeImgTxtLabel, Button1Label, Button1LinkLabel, Button2TxtLabel, Button2LinkLabel;

    private ImageView delayTooSmallExceptionView;

    private ImageView invalidInputExceptionView;


    private int numberInList = -1;

    public void cancelSaves() {
        onFinish();
    }

    public void saveChanges() {
        try{
            Script.getINSTANCE().setUpdates(numberInList, new Updates(Long.parseLong(Wait.getText()), image.getText(), imagetext.getText(), smallimage.getText(),
                    smalltext.getText(), firstline.getText(), secondline.getText(), button1Text.getText(),
                    button1Url.getText(), button2Text.getText(), button2Url.getText()));
        }catch (NumberFormatException e){
            if (!anchorRoot.getChildren().contains(invalidInputExceptionView)) {
                invalidInputExceptionView = WarningManager.setWarning(DelayLabel, 12, "Invalid input", WarningManager.Mode.Right);
                anchorRoot.getChildren().add(invalidInputExceptionView);
            }
            new Shake(anchorRoot).play();
            return;
        }
        anchorRoot.getChildren().remove(invalidInputExceptionView);
        onFinish();
    }

    public void deleteThisItem() {
        Script.getINSTANCE().getTotalupdates().remove(numberInList);
        onFinish();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Wait.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                Wait.setText(newValue.replaceAll("\\D", ""));
            if (Wait.getText().isEmpty()) return;
            if (Long.parseLong(Wait.getText()) < 16000) {
                if (!anchorRoot.getChildren().contains(delayTooSmallExceptionView)) {
                    delayTooSmallExceptionView =
                            WarningManager.setWarning(DelayLabel, 12, "It is recommended to set the delay above 16 second", WarningManager.Mode.Right);
                    anchorRoot.getChildren().add(delayTooSmallExceptionView);
                }
            } else anchorRoot.getChildren().remove(delayTooSmallExceptionView);
        });
        Platform.runLater(() ->{
            stage = (Stage) anchorRoot.getScene().getWindow();
            stage.setOnCloseRequest(e ->{
                e.consume();
                cancelSaves();
            });
        });
    }

    public void numberInList(int numberInList) {
        this.numberInList = numberInList;
        final Script scriptInstance = Script.getINSTANCE();
        Wait.setText(String.valueOf(scriptInstance.getTotalupdates().get(numberInList).getWait()));
        image.setText(scriptInstance.getTotalupdates().get(numberInList).getImage());
        imagetext.setText(scriptInstance.getTotalupdates().get(numberInList).getImagetext());
        smallimage.setText(scriptInstance.getTotalupdates().get(numberInList).getSmallimage());
        smalltext.setText(scriptInstance.getTotalupdates().get(numberInList).getSmalltext());
        firstline.setText(scriptInstance.getTotalupdates().get(numberInList).getFl());
        secondline.setText(scriptInstance.getTotalupdates().get(numberInList).getSl());
        button1Text.setText(scriptInstance.getTotalupdates().get(numberInList).getButton1Text());
        button1Url.setText(scriptInstance.getTotalupdates().get(numberInList).getButton1Url());
        button2Text.setText(scriptInstance.getTotalupdates().get(numberInList).getButton2Text());
        button2Url.setText(scriptInstance.getTotalupdates().get(numberInList).getButton2Url());


    }

    private void onFinish() {
        stage.close();
        numberInList = -1;
    }

    public static void showListConfig(int numberInList, double x, double y) {
        SceneManager.SceneData sceneData = SceneManager.loadSceneWithStyleSheet("/lee/aspect/dev/dynamicrp/scenes/EditListScript.fxml");
        EditListController ec = (EditListController) sceneData.getController();
        ec.numberInList(numberInList);
        Stage stage = new Stage();
        stage.getIcons().add(new Image(Objects.requireNonNull(EditListController.class.getResourceAsStream("/lee/aspect/dev/dynamicrp/icon/settingsImage.png"))));
        stage.setTitle("Config Editor - index: " + (numberInList + 1));
        stage.setScene(new Scene(sceneData.getRoot()));
        stage.setX(x);
        stage.setY(y);
        stage.setResizable(false);
        stage.show();
    }

}
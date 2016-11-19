package application;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.event.EventHandler;

public class CubicCurveDemo extends AnchorPane {

	/**
	 * FXML initialization requirement
	 */

	@FXML
	private Circle curveEnd;
	@FXML
	private Circle curveStart;
	@FXML
	private Circle curveC1;
	@FXML
	private Circle curveC2;
	
	@FXML
	private Line mLt_start_c1;
	@FXML
	private Line mLt_c2_end;
	
	@FXML
	private CubicCurve mCurve;

	
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	
	public CubicCurveDemo () {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/CubicCurveDemo.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			fxmlLoader.load();
        
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}		
	}
	
	@FXML
	private void initialize() {
		
		initCurves();

	}
	
	@FXML
	private void labelMouseOver() {
		Background b = new Background(new BackgroundFill(null, null, null));
		
	}
	
	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		relocate ( 
				(int) (localCoords.getX() - mDragOffset.getX()),
				(int) (localCoords.getY() - mDragOffset.getY())
			);
	}	
	
	private void initCurves() {
		
		// bind control lines to circle centers
		mLt_start_c1.startXProperty().bind(mCurve.startXProperty());
		mLt_start_c1.startYProperty().bind(mCurve.startYProperty());

		mLt_start_c1.endXProperty().bind(mCurve.controlX1Property());
		mLt_start_c1.endYProperty().bind(mCurve.controlY1Property());

		mLt_c2_end.startXProperty().bind(mCurve.controlX2Property());
		mLt_c2_end.startYProperty().bind(mCurve.controlY2Property());

		mLt_c2_end.endXProperty().bind(mCurve.endXProperty());
		mLt_c2_end.endYProperty().bind(mCurve.endYProperty());

		// bind curve to circle centers
		mCurve.startXProperty().bind(curveStart.centerXProperty());
		mCurve.startYProperty().bind(curveStart.centerYProperty());

		mCurve.controlX1Property().bind(curveC1.centerXProperty());
		mCurve.controlY1Property().bind(curveC1.centerYProperty());

		mCurve.controlX2Property().bind(curveC2.centerXProperty());
		mCurve.controlY2Property().bind(curveC2.centerYProperty());

		mCurve.endXProperty().bind(curveEnd.centerXProperty());
		mCurve.endYProperty().bind(curveEnd.centerYProperty());

		curveStart.setCenterX(10.0f);
		curveStart.setCenterY(10.0f);

		curveC1.setCenterX(20.0f);
		//curveC1.centerXProperty().bind(Bindings.add(150.0f, curveStart.centerXProperty()));
		//curveC1.centerYProperty().bind(curveStart.centerYProperty());

		curveC2.setCenterX(50.0f);
		//curveC2.centerXProperty().bind(Bindings.add(-150.0f, curveEnd.centerXProperty()));
		//curveC2.centerYProperty().bind(curveEnd.centerYProperty());

		curveEnd.setCenterX(40.0f);
		curveEnd.setCenterY(40.0f);		
	}

	@FXML
	private void updateCurveStart(MouseEvent event) {

		curveStart.setCenterX(event.getX());
		curveStart.setCenterY(event.getY());
	}

	@FXML
	private void updateCurveC1(MouseEvent event) {

		curveC1.setCenterX(event.getX());
		curveC1.setCenterY(event.getY());
	}

	@FXML
	private void updateCurveC2(MouseEvent event) {

		curveC2.setCenterX(event.getX());
		curveC2.setCenterY(event.getY());

	}

	@FXML
	private void updateCurveEnd(MouseEvent event) {

		curveEnd.setCenterX(event.getX());
		curveEnd.setCenterY(event.getY());

	}
}

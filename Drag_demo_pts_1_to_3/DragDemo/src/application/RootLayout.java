package application;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class RootLayout extends AnchorPane{

	@FXML SplitPane base_pane;
	@FXML AnchorPane right_pane;
	@FXML VBox left_pane;

	private DragIcon mDragOverIcon = null;
	
	private EventHandler<DragEvent> mIconDragOverRoot = null;
	private EventHandler<DragEvent> mIconDragDropped = null;
	private EventHandler<DragEvent> mIconDragOverRightPane = null;
	
	public RootLayout() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/RootLayout.fxml")
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
		
		//Add one icon that will be used for the drag-drop process
		//This is added as a child to the root anchorpane so it can be visible
		//on both sides of the split pane.
		mDragOverIcon = new DragIcon();
		
		mDragOverIcon.setVisible(false);
		mDragOverIcon.setOpacity(0.65);
		getChildren().add(mDragOverIcon);
		
		//populate left pane with multiple colored icons for testing
		for (int i = 0; i < 7; i++) {
			
			DragIcon icn = new DragIcon();
			
			addDragDetection(icn);
			
			icn.setType(DragIconType.values()[i]);
			left_pane.getChildren().add(icn);
		}
		
		buildDragHandlers();
	}
	
	private void addDragDetection(DragIcon dragIcon) {
		
		dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				// set drag event handlers on their respective objects
				base_pane.setOnDragOver(mIconDragOverRoot);
				right_pane.setOnDragOver(mIconDragOverRightPane);
				right_pane.setOnDragDropped(mIconDragDropped);
				
				// get a reference to the clicked DragIcon object
				DragIcon icn = (DragIcon) event.getSource();
				
				//begin drag ops
				mDragOverIcon.setType(icn.getType());
				mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
            
				ClipboardContent content = new ClipboardContent();
				DragContainer container = new DragContainer();
				
				container.addData ("type", mDragOverIcon.getType().toString());
				content.put(DragContainer.AddNode, container);

				mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
				mDragOverIcon.setVisible(true);
				mDragOverIcon.setMouseTransparent(true);
				event.consume();					
			}
		});
	}	
	
	private void buildDragHandlers() {
		
		//drag over transition to move widget form left pane to right pane
		mIconDragOverRoot = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				//turn on transfer mode and track in the right-pane's context 
				//if (and only if) the mouse cursor falls within the right pane's bounds.
				if (!right_pane.boundsInLocalProperty().get().contains(p)) {
					
					event.acceptTransferModes(TransferMode.ANY);
					mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					return;
				}

				event.consume();
			}
		};
		
		mIconDragOverRightPane = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);
				
				//convert the mouse coordinates to scene coordinates,
				//then convert back to coordinates that are relative to 
				//the parent of mDragIcon.  Since mDragIcon is a child of the root
				//pane, coodinates must be in the root pane's coordinate system to work
				//properly.
				mDragOverIcon.relocateToPoint(
								new Point2D(event.getSceneX(), event.getSceneY())
				);
				event.consume();
			}
		};
				
		mIconDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {
				
				DragContainer container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
				
				container.addData("scene_coords", 
						new Point2D(event.getSceneX(), event.getSceneY()));
				
				ClipboardContent content = new ClipboardContent();
				content.put(DragContainer.AddNode, container);
				
				event.getDragboard().setContent(content);
				event.setDropCompleted(true);
			}
		};

		this.setOnDragDone (new EventHandler <DragEvent> (){
			
			@Override
			public void handle (DragEvent event) {
				
				right_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRightPane);
				right_pane.removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
				base_pane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
								
				mDragOverIcon.setVisible(false);
				
				DragContainer container = 
						(DragContainer) event.getDragboard().getContent(DragContainer.AddNode);
				
				if (container != null) {
					if (container.getValue("scene_coords") != null) {
					
						DragIcon droppedIcon = new DragIcon();
						
						droppedIcon.setType(DragIconType.valueOf(container.getValue("type")));
						right_pane.getChildren().add(droppedIcon);

						Point2D cursorPoint = container.getValue("scene_coords");

						droppedIcon.relocateToPoint(
								new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
								);
					}
				}

				event.consume();
			}
		});
	}
	/*
	public void buildSplitPaneDragHandlers() {
		
		//drag detection for widget in the left-hand scroll pane to create a node in the right pane 
		mWidgetDragDetected = new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				fs_right_pane.setOnDragDropped(null);
				fs_root.setOnDragOver(null);
				fs_right_pane.setOnDragOver(null);
				
				fs_right_pane.setOnDragDropped(mRightPaneDragDropped);
				fs_root.setOnDragOver(mRootDragOver);
				
                //begin drag ops

                mDragObject = ((IFileSystemObject) (event.getSource())).getDragObject();
                
                if (!fs_root.getChildren().contains((Node)mDragObject))
                	fs_root.getChildren().add((Node)mDragObject);
                
                mDragObject.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
                
                ClipboardContent content = new ClipboardContent();
                content.putString(mDragObject.getFileSystemType().toString());

                mDragObject.startDragAndDrop (TransferMode.ANY).setContent(content);
                mDragObject.setVisible(true);
                
                event.consume();					
			}					
		};
		
		//drag over transition to move widget form left pane to right pane
		mRootDragOver = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				
				Point2D p = fs_right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());

				if (!fs_right_pane.boundsInLocalProperty().get().contains(p)) {
					mDragObject.relocateToPoint(new Point2D(event.getX(), event.getY()));
					return;
				}

				fs_root.removeEventHandler(DragEvent.DRAG_OVER, this);
				fs_right_pane.setOnDragOver(mRightPaneDragOver);
				event.consume();

			}
		};
		
		//drag over in the right pane
		mRightPaneDragOver = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				event.acceptTransferModes(TransferMode.ANY);
				mDragObject.relocateToPoint(mDragObject.getParent().sceneToLocal(new Point2D(event.getSceneX(), event.getSceneY())));
				
				event.consume();
			}
		};		
		
		//drop action in the right pane to create a new node
		mRightPaneDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {
				Point2D p = fs_right_pane.sceneToLocal(new Point2D (event.getSceneX(), event.getSceneY()));	
				
				self.addFileSystemNode(mDragObject.getFileSystemType(), p);
				event.setDropCompleted(true);

				fs_right_pane.setOnDragOver(null);
				fs_right_pane.setOnDragDropped(null);
				fs_root.setOnDragOver(null);
				
				event.consume();
			}
		};		
	}		*/
}

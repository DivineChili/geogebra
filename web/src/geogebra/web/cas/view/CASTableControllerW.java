package geogebra.web.cas.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class CASTableControllerW implements
MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler, DoubleClickHandler{

	private CASTableW table;
	public CASTableControllerW(CASTableW consoleTable) {
	    table = consoleTable;
    }

	public void onDoubleClick(DoubleClickEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onClick(ClickEvent event) {
	    int row =table.getCellForEvent(event).getRowIndex();
	    table.startEditingRow(row);
	    
    }

	public void onMouseMove(MouseMoveEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onMouseUp(MouseUpEvent event) {
	    // TODO Auto-generated method stub
	    
    }

	public void onMouseDown(MouseDownEvent event) {
	    // TODO Auto-generated method stub
	    
    }

}

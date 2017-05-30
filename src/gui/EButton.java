package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class EButton {

	private Rectangle rect;
	private String text;

	private boolean mouseOn;
	private boolean active;

	// colors
	private Color unselected = Color.cyan;
	private Color selected = Color.LIGHT_GRAY;
	private Color disabled = Color.DARK_GRAY;

	public void draw(Graphics2D g){
		if(!active)g.setColor(disabled);
		else if(!mouseOn)g.setColor(unselected);
		else if(mouseOn)g.setColor(selected);

		g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

		g.setColor(Color.black);
		g.drawString(text, rect.x+10, rect.y+20);
	}

	public EButton(Rectangle rect, String text) {
		super();
		this.rect = rect;
		this.text = text;
		this.active = true;
		this.mouseOn = false;
	}

	public Rectangle getRect() {
		return rect;
	}

	public String getText() {
		return text;
	}

	public Color getBack() {
		return unselected;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setMouseOn(boolean b) {
		this.mouseOn = b;
	}

	public boolean isOn(Point p){
		//Point p = new Point(x,y);
		if(this.rect.contains(p)){
			return true;
		}
		return false;
	}

	public boolean isActive() {
		return active;
	}

}

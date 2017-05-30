package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class RButton {

	private Rectangle rec;
	private boolean active;

	private int type;

	public static final int TYPE_MAIL = 0;
	public static final int TYPE_CUSTOMERPRICE = 1;
	public static final int TYPE_DISCONTINUECUSTOMERPRICE = 2;
	public static final int TYPE_ROUTE = 3;
	public static final int TYPE_DISCONTINUEROUTE = 4;

	public static final Image IMAGE_MAIL = new ImageIcon("res/ribbonPanel/Mail.png").getImage();
	public static final Image IMAGE_CUSTOMERPRICE = new ImageIcon("res/ribbonPanel/CusPrice.png").getImage();
	public static final Image IMAGE_DISCONTINUECUSTOMERPRICE = new ImageIcon("res/ribbonPanel/DeCusPrice.png").getImage();
	public static final Image IMAGE_ROUTE = new ImageIcon("res/ribbonPanel/Route.png").getImage();
	public static final Image IMAGE_DISCONTINUEROUTE = new ImageIcon("res/ribbonPanel/Deroute.png").getImage();
	private final int iconWidth = 70;

	public static final Image IMAGE_BACK1 = new ImageIcon("res/ribbonPanel/RButtonBack.png").getImage();
	public static final Image IMAGE_BACK2 = new ImageIcon("res/ribbonPanel/RButtonBack1.png").getImage();

	public void draw(Graphics2D g) {
		if(rec==null){
			System.err.println("Button rectangle not specified");
			return;
		}
		g.setColor(Color.blue);
		if(this.active){
			g.drawImage(IMAGE_BACK2,this.rec.x, this.rec.y, this.rec.width, this.rec.height,null);
		}else{
			g.drawImage(IMAGE_BACK1,this.rec.x, this.rec.y, this.rec.width, this.rec.height,null);
		}
		int mid = this.rec.width/2;
		int x = mid - (iconWidth/2);
		switch (type) {
		case TYPE_MAIL:
			//g.drawImage(IMAGE_MAIL, this.rec.x+90, this.rec.y+10, this.rec.width-180, this.rec.height-20, null);
			g.drawImage(IMAGE_MAIL, this.rec.x+x, this.rec.y+10, iconWidth, this.rec.height-20, null);
			break;
		case TYPE_CUSTOMERPRICE:
			g.drawImage(IMAGE_CUSTOMERPRICE, this.rec.x+x, this.rec.y+10, iconWidth, this.rec.height-20, null);
			break;
		case TYPE_DISCONTINUECUSTOMERPRICE:
			g.drawImage(IMAGE_DISCONTINUECUSTOMERPRICE, this.rec.x+x, this.rec.y+10, iconWidth, this.rec.height-20, null);
			break;
		case TYPE_ROUTE:
			g.drawImage(IMAGE_ROUTE, this.rec.x+x, this.rec.y+10, iconWidth, this.rec.height-20, null);
			break;
		case TYPE_DISCONTINUEROUTE:
			g.drawImage(IMAGE_DISCONTINUEROUTE, this.rec.x+x, this.rec.y+10, iconWidth, this.rec.height-20, null);
			break;
		}
	}

	public RButton(int type) {
		this.type = type;
	}

	public boolean isOn(Point p) {
		if (this.rec.contains(p)) {
			this.active = true;
			return true;
		}else{
			this.active = false;
			return false;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setRectangle(Rectangle rec){
		this.rec = rec;
	}

	public void action(){
		switch (type) {
		case TYPE_MAIL:
			System.out.println(">>> new mail");
			FileMenuListener.newMail();
			break;
		case TYPE_CUSTOMERPRICE:
			System.out.println(">>> new cus price");
			FileMenuListener.newCustomerPriceUpdateEvent();
			break;
		case TYPE_DISCONTINUECUSTOMERPRICE:
			System.out.println(">>> dis cus price");
			FileMenuListener.discontinueCustomerPrice();
			break;
		case TYPE_ROUTE:
			System.out.println(">>> new route");
			FileMenuListener.newRoute();
			break;
		case TYPE_DISCONTINUEROUTE:
			System.out.println(">>> dis route");
			FileMenuListener.discontinueRoute();
			break;
		}
	}
}

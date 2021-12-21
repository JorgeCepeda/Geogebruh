package otros;

import java.awt.*;
import java.awt.image.*;

import javax.swing.JPanel;

public class AWTImage extends JPanel {
	private static final long serialVersionUID = 1L;
	private Image img;

    public AWTImage(BufferedImage img) {
    	setImage(img);
	}
    
    public Image getImage() {
		return img;
	}
    
    public void setImage(Image scaledImage) {
    	img = scaledImage;
    	repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
    	g.clearRect(0, 0, getWidth(), getHeight());
    	if (img != null) g.drawImage(img, 0, 0, null);
    }

    @Override
    public Dimension getPreferredSize() {
    	if (img == null) return new Dimension(100, 100);
    	return new Dimension(img.getWidth(null), img.getHeight(null));
    }        
}
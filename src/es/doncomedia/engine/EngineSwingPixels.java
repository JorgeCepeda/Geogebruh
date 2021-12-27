package es.doncomedia.engine;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;

import es.doncomedia.chunks.Chunks;
import es.doncomedia.effects.Lighting;
import es.doncomedia.engine.menus.MainMenu;
import es.doncomedia.graphics.MultithreadedScreen;
import es.doncomedia.graphics.Screen;
import es.doncomedia.levels.Levels;
import es.doncomedia.misc.AWTImage;
import es.doncomedia.objects.Camera;
import es.doncomedia.objects.Objects;
import es.doncomedia.operations.MyMath;

import java.awt.Font;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Color;

public class EngineSwingPixels implements GFXEngine<Image> {
	private double[] pos;
	private double thetaHori, thetaVert, thetaInclin, thetaDelta = Math.PI/8;
	private boolean swBorders, swPrecision;
	
	private JFrame frmGFXEngine;
	private JTextField txtRender;
	private JTextField txtFOV;
	private AWTImage imgOutput;
	private Screen screen;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				long start = System.nanoTime();
				EngineSwingPixels window = new EngineSwingPixels();
				window.frmGFXEngine.setVisible(true);
				System.out.println("Ha tardado " + (System.nanoTime() - start) + " nanosegundos en cargar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EngineSwingPixels() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Levels.addListener(this);
		Objects.init();
		Chunks.load(pos, 3);
		
		frmGFXEngine = new JFrame("exe.exe - Geoge🅱ruh");
		frmGFXEngine.setResizable(false);
		frmGFXEngine.setBounds(100, 1, 738, 723);
		frmGFXEngine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGFXEngine.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBounds(10, 69, 712, 614);
		frmGFXEngine.add(panel);
		panel.setLayout(null);
		
		imgOutput = new AWTImage(null);
		imgOutput.setBackground(Color.BLACK);
		imgOutput.setBounds(10, 69, 712, 614);
		frmGFXEngine.add(imgOutput);
		imgOutput.setLayout(null);
		
		JLabel lblPosition = new JLabel(String.format("Posición inicial (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
		lblPosition.setBounds(149, 6, 301, 20);
		frmGFXEngine.getContentPane().add(lblPosition);
		
		txtRender = new JTextField("10");
		txtRender.setBounds(104, 6, 35, 20);
		frmGFXEngine.add(txtRender);
		txtRender.setColumns(10);
		
		txtFOV = new JTextField("N");
		txtFOV.setBounds(638, 6, 43, 20);
		frmGFXEngine.add(txtFOV);
		txtFOV.setColumns(10);
		
		JButton btnExecute = new JButton("Ejecutar");
		btnExecute.addActionListener(arg0 -> {
			// Empty
		});
		btnExecute.setBounds(10, 32, 84, 23);
		frmGFXEngine.add(btnExecute);
		
		JLabel lblLegend = new JLabel("(WASDC y Espacio = movimiento|QERTVB = rotaciones)");
		lblLegend.setBounds(149, 33, 322, 20);
		frmGFXEngine.add(lblLegend);
		
		JLabel lblRenderDistance = new JLabel("Renderizado/5:");
		lblRenderDistance.setBounds(10, 6, 90, 20);
		frmGFXEngine.add(lblRenderDistance);
		
		JLabel lblFieldOfView = new JLabel("Campo de visión (grados|N):");
		lblFieldOfView.setBounds(460, 6, 168, 20);
		frmGFXEngine.add(lblFieldOfView);
		
		JButton btnBorders = new JButton("Bordes");
		btnBorders.addActionListener(arg0 -> swBorders = !swBorders);
		btnBorders.setBounds(633, 32, 89, 23);
		frmGFXEngine.add(btnBorders);
		
		JButton btnPrecision = new JButton("Precisión: OFF");
		btnPrecision.addActionListener(arg0 -> {
			if (swPrecision = !swPrecision) btnPrecision.setText("Precisión: ON");
			else btnPrecision.setText("Precisión: OFF");
		});
		btnPrecision.setBounds(508, 32, 115, 23);
		frmGFXEngine.add(btnPrecision);
		
//		MainMenu menu = new MainMenu(this);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED && frmGFXEngine.getFocusOwner() == btnExecute) {
//				boolean menuHidden = false;
//				if (menu.isShown()) menuHidden = menu.executeInput(e.getKeyCode());
//				else {
					switch (e.getKeyCode()) {
//					case KeyEvent.VK_M:
//						menu.setShown(true);
//						break;
					case KeyEvent.VK_W:
						pos[0] += Math.cos(thetaHori);
						pos[2] += Math.sin(thetaHori);
						break;
					case KeyEvent.VK_A:
						pos[0] -= Math.sin(thetaHori);
						pos[2] += Math.cos(thetaHori);
						break;
					case KeyEvent.VK_S:
						pos[0] -= Math.cos(thetaHori);
						pos[2] -= Math.sin(thetaHori);
						break;
					case KeyEvent.VK_D:
						pos[0] += Math.sin(thetaHori);
						pos[2] -= Math.cos(thetaHori);
						break;
					case KeyEvent.VK_SPACE:
						pos[1]++;
						break;
					case KeyEvent.VK_C:
						pos[1]--;
						break;
					case KeyEvent.VK_Q:
						thetaHori += thetaDelta;
						break;
					case KeyEvent.VK_E:
						thetaHori -= thetaDelta;
						break;
					case KeyEvent.VK_R:
						thetaVert -= thetaDelta;
						break;
					case KeyEvent.VK_T:
						thetaVert += thetaDelta;
						break;
					case KeyEvent.VK_V:
						thetaInclin -= thetaDelta;
						break;
					case KeyEvent.VK_B:
						thetaInclin += thetaDelta;
						break;
					default:
						System.out.println("Input no reconocido");
					}
					thetaHori = MyMath.fix(thetaHori);
					thetaVert = MyMath.fix(thetaVert);
					thetaInclin = MyMath.fix(thetaInclin);
					pos = MyMath.fix(pos);
					
					lblPosition.setText(String.format("Posición actual: (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
//				}
//				if (!menu.isShown() && !menuHidden) {
					// Render
					int render = Integer.parseInt(txtRender.getText()) * 5;
					Camera camera = new Camera(pos, render, swPrecision);
					camera.setRotationAndOrient(new double[] {thetaHori, thetaVert, thetaInclin});
					if (txtFOV.getText().trim().toLowerCase().charAt(0) != 'n') camera.setFOVTheta(MyMath.fix(Double.parseDouble(txtFOV.getText()) * Math.PI / 180));
					
					//screen = new Screen(51, 71, swBorders, camera); // Slower, lighter, 1 thread
					screen = new MultithreadedScreen(51, 71, 4, swBorders, camera);
					screen.setLightUp(Lighting.isLightingOn());
					
					long start = System.nanoTime();
					screen.render();
					imgOutput.setImage(screen.getFrame(false).getScaledInstance(imgOutput.getWidth(), imgOutput.getHeight(), Image.SCALE_SMOOTH));
					System.out.println("Tiempo transcurrido: " + (System.nanoTime() - start) + " nanosegundos");
//				}
			}
	        return false;
		});
	}

	@Override
	public void react() {
		pos = Levels.loaded().getPos();
		double[] rotations = Levels.loaded().getRotation();
		thetaHori = rotations[0];
		thetaVert = rotations[1];
		thetaInclin = rotations[2];
	}

	@Override
	public Screen getScreen() {
		return screen;
	}
	
	@Override
	public Image getOutput() {
		return imgOutput.getImage();
	}
}
package es.doncomedia.engine;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import es.doncomedia.chunks.Chunks;
import es.doncomedia.effects.Lighting;
import es.doncomedia.engine.menus.MainMenu;
import es.doncomedia.graphics.MultithreadedScreen;
import es.doncomedia.graphics.Screen;
import es.doncomedia.levels.Levels;
import es.doncomedia.objects.Camera;
import es.doncomedia.objects.Objects;
import es.doncomedia.operations.MyMath;

import java.awt.Font;
import java.awt.Color;

public class EngineSwingASCII implements GFXEngine<JLabel> {
	private double[] pos;
	private double thetaHori, thetaVert, thetaInclin, thetaDelta = Math.PI/8;
	private boolean swBorders, swPrecision;
	
	private JFrame frmGFXEngine;
	private JTextField txtRender;
	private JTextField txtInstruction;
	private JTextField txtFOV;
	private JLabel lblOutput;
	private Screen screen;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				long start = System.nanoTime();
				EngineSwingASCII window = new EngineSwingASCII();
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
	public EngineSwingASCII() {
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
		
		lblOutput = new JLabel("Introduce los datos necesarios");
		lblOutput.setForeground(Color.GREEN);
		lblOutput.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblOutput.setHorizontalAlignment(SwingConstants.CENTER);
		lblOutput.setBounds(0, 0, 712, 614);
		panel.add(lblOutput);
		
		JLabel lblPosition = new JLabel(String.format("Posición inicial (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
		lblPosition.setBounds(149, 6, 301, 20);
		frmGFXEngine.add(lblPosition);
		
		txtRender = new JTextField("10");
		txtRender.setBounds(104, 6, 35, 20);
		frmGFXEngine.add(txtRender);
		txtRender.setColumns(10);
		
		txtFOV = new JTextField("N");
		txtFOV.setBounds(638, 6, 43, 20);
		frmGFXEngine.add(txtFOV);
		txtFOV.setColumns(10);

		txtInstruction = new JTextField("->");
		txtInstruction.setHorizontalAlignment(SwingConstants.CENTER);
		txtInstruction.setBounds(104, 32, 35, 23);
		frmGFXEngine.add(txtInstruction);
		txtInstruction.setColumns(10);
		
		MainMenu menu = new MainMenu(this);
		
		JButton btnExecute = new JButton("Ejecutar");
		btnExecute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean menuHidden = executeInput(txtInstruction.getText().toLowerCase().charAt(0));
				
				if (!menu.isDisplayed() && !menuHidden) {
					// Render
					int render = Integer.parseInt(txtRender.getText()) * 5;
					Camera camera = new Camera(pos, render, swPrecision);
					camera.setRotationAndOrient(new double[] {thetaHori, thetaVert, thetaInclin});
					if (txtFOV.getText().trim().toLowerCase().charAt(0) != 'n') camera.setFOVTheta(MyMath.fix(Double.parseDouble(txtFOV.getText()) / 180 * Math.PI));
					
					//screen = new Screen(51, 71, swBorder, camera); // Slower, lighter, 1 thread
					screen = new MultithreadedScreen(51, 71, 4, swBorders, camera);
					screen.setLightUp(Lighting.isLightingOn());
					
					long start = System.nanoTime();
					screen.render();
					lblOutput.setText(screen.getRenderText());
					System.out.println("Tiempo de dibujado: " + (System.nanoTime() - start) + " nanosegundos");
				}
			}

			private boolean executeInput(char input) {
				if (menu.isDisplayed()) return menu.executeInput(input);
				switch (input) {
				case 'm':
					menu.setDisplayed(true);
					break;
				case 'w':
					pos[0] += Math.cos(thetaHori);
					pos[2] += Math.sin(thetaHori);
					break;
				case 'a':
					pos[0] -= Math.sin(thetaHori);
					pos[2] += Math.cos(thetaHori);
					break;
				case 's':
					pos[0] -= Math.cos(thetaHori);
					pos[2] -= Math.sin(thetaHori);
					break;
				case 'd':
					pos[0] += Math.sin(thetaHori);
					pos[2] -= Math.cos(thetaHori);
					break;
				case ' ':
					pos[1]++;
					break;
				case 'c':
					pos[1]--;
					break;
				case 'q':
					thetaHori += thetaDelta;
					break;
				case 'e':
					thetaHori -= thetaDelta;
					break;
				case 'r':
					thetaVert -= thetaDelta;
					break;
				case 't':
					thetaVert += thetaDelta;
					break;
				case 'v':
					thetaInclin -= thetaDelta;
					break;
				case 'b':
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
				return false;
			}
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
		btnBorders.addActionListener(e -> swBorders = !swBorders);
		btnBorders.setBounds(633, 32, 89, 23);
		frmGFXEngine.add(btnBorders);
		
		JButton btnPrecision = new JButton("Precisión: OFF");
		btnPrecision.addActionListener(e -> {
			if (swPrecision = !swPrecision) btnPrecision.setText("Precisión: ON");
			else btnPrecision.setText("Precisión: OFF");
		});
		btnPrecision.setBounds(508, 32, 115, 23);
		frmGFXEngine.add(btnPrecision);
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
	public JLabel getOutput() {
		return lblOutput;
	}
}
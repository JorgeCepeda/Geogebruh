package motor_gráfico;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;

import chunks_NoCeldas.Chunks;
import efectos.Iluminación;
import gráficos.Pantalla;
import gráficos.PantallaMultihilo;
import motor_gráfico.menús.MenúPrincipal;
import niveles.Niveles;

import java.awt.KeyboardFocusManager;
import java.awt.Color;

import objetos.Cámara;
import objetos.Objetos;
import operaciones.MyMath;
import otros.AWTImage;

public class MotorGráfico3D_AWT implements MotorGráfico<Image> {
	private double[] pos;
	private double teta_hori, teta_vert, teta_inclin, delta_teta = Math.PI/8;
	private boolean swBorde, swPrecisión;
	
	private JFrame frmMotorGráfico;
	private JTextField txtRender;
	private JTextField txtCDV;
	private AWTImage imgSalida;
	private Pantalla pantalla;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				long start = System.nanoTime();
				MotorGráfico3D_AWT window = new MotorGráfico3D_AWT();
				window.frmMotorGráfico.setVisible(true);
				System.out.println("Ha tardado " + (System.nanoTime() - start) + " nanosegundos en cargar");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MotorGráfico3D_AWT() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Niveles.añadirListener(this);
		Objetos.init();
		Chunks.cargar(pos, 3);
		
		frmMotorGráfico = new JFrame();
		frmMotorGráfico.setTitle("exe.exe - Geoge🅱ruh");
		frmMotorGráfico.setResizable(false);
		frmMotorGráfico.setBounds(100, 1, 738, 723);
		frmMotorGráfico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMotorGráfico.setLayout(null);
		
		imgSalida = new AWTImage(null);
		imgSalida.setBackground(Color.BLACK);
		imgSalida.setBounds(10, 69, 712, 614);
		frmMotorGráfico.add(imgSalida);
		imgSalida.setLayout(null);
		
		JLabel lblPosición = new JLabel(String.format("Posición inicial (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
		lblPosición.setBounds(149, 6, 301, 20);
		frmMotorGráfico.getContentPane().add(lblPosición);
		
		txtRender = new JTextField();
		txtRender.setText("10");
		txtRender.setBounds(104, 6, 35, 20);
		frmMotorGráfico.add(txtRender);
		txtRender.setColumns(10);
		
		txtCDV = new JTextField();
		txtCDV.setText("N");
		txtCDV.setBounds(638, 6, 43, 20);
		frmMotorGráfico.add(txtCDV);
		txtCDV.setColumns(10);
		
		JButton btnEjecutar = new JButton("Ejecutar");
		btnEjecutar.addActionListener(arg0 -> {
		});
		btnEjecutar.setBounds(10, 32, 84, 23);
		frmMotorGráfico.add(btnEjecutar);
		
		JLabel lblLeyenda = new JLabel("(WASDC y Espacio = movimiento|QERTVB = rotaciones)");
		lblLeyenda.setBounds(149, 33, 322, 20);
		frmMotorGráfico.add(lblLeyenda);
		
		JLabel lblRenderizado = new JLabel("Renderizado/5:");
		lblRenderizado.setBounds(10, 6, 90, 20);
		frmMotorGráfico.add(lblRenderizado);
		
		JLabel lblCampoDeVisión = new JLabel("Campo de visión (grados|N):");
		lblCampoDeVisión.setBounds(460, 6, 168, 20);
		frmMotorGráfico.add(lblCampoDeVisión);
		
		JButton btnBordes = new JButton("Bordes");
		btnBordes.addActionListener(arg0 -> swBorde = !swBorde);
		btnBordes.setBounds(633, 32, 89, 23);
		frmMotorGráfico.add(btnBordes);
		
		JButton btnPrecisión = new JButton("Precisión: OFF");
		btnPrecisión.addActionListener(arg0 -> {
			if (swPrecisión = !swPrecisión) btnPrecisión.setText("Precisión: ON");
			else btnPrecisión.setText("Precisión: OFF");
		});
		btnPrecisión.setBounds(508, 32, 115, 23);
		frmMotorGráfico.add(btnPrecisión);
		
//		MenúPrincipal menú = new MenúPrincipal(this);
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED && frmMotorGráfico.getFocusOwner() == btnEjecutar) {
//				boolean menú_ocultado = false;
//				if (menú.estáMostrado()) menú_ocultado = menú.ejecutarInput(e.getKeyCode());
//				else {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_M:
//						menú.setMostrado(true); //TODO añadir soporte
						break;
					case KeyEvent.VK_W:
						pos[0] += Math.cos(teta_hori);
						pos[2] += Math.sin(teta_hori);
						break;
					case KeyEvent.VK_A:
						pos[0] -= Math.sin(teta_hori);
						pos[2] += Math.cos(teta_hori);
						break;
					case KeyEvent.VK_S:
						pos[0] -= Math.cos(teta_hori);
						pos[2] -= Math.sin(teta_hori);
						break;
					case KeyEvent.VK_D:
						pos[0] += Math.sin(teta_hori);
						pos[2] -= Math.cos(teta_hori);
						break;
					case KeyEvent.VK_SPACE:
						pos[1]++;
						break;
					case KeyEvent.VK_C:
						pos[1]--;
						break;
					case KeyEvent.VK_Q:
						teta_hori += delta_teta;
						break;
					case KeyEvent.VK_E:
						teta_hori -= delta_teta;
						break;
					case KeyEvent.VK_R:
						teta_vert -= delta_teta;
						break;
					case KeyEvent.VK_T:
						teta_vert += delta_teta;
						break;
					case KeyEvent.VK_V:
						teta_inclin -= delta_teta;
						break;
					case KeyEvent.VK_B:
						teta_inclin += delta_teta;
						break;
					default:
						System.out.println("Input no reconocido");
					}
					teta_hori = MyMath.fix(teta_hori);
					teta_vert = MyMath.fix(teta_vert);
					teta_inclin = MyMath.fix(teta_inclin);
					pos = MyMath.fix(pos);
					
					lblPosición.setText(String.format("Posición actual: (%+.2f,%+.2f,%+.2f)", pos[0], pos[1], pos[2]));
//				}
//				if (!menú.estáMostrado() && !menú_ocultado) {
					// Renderizar
					int render = Integer.parseInt(txtRender.getText()) * 5;
					Cámara cámara = new Cámara(pos, render, swPrecisión);
					cámara.setRotaciónYOrient(new double[] {teta_hori, teta_vert, teta_inclin});
					if (txtCDV.getText().trim().toLowerCase().charAt(0) != 'n') cámara.setTeta_CDV(MyMath.fix(Double.parseDouble(txtCDV.getText()) * Math.PI / 180));
					
					//pantalla = new Pantalla(51, 71, swBorde, cámara); // Más lento, más ligero, 1 hilo
					pantalla = new PantallaMultihilo(51, 71, 4, swBorde, cámara);
					pantalla.setIluminada(Iluminación.hayIluminación());
					
					long start = System.nanoTime();
					pantalla.renderizar();
					imgSalida.setImage(pantalla.obtenerFrame(false).getScaledInstance(imgSalida.getWidth(), imgSalida.getHeight(), Image.SCALE_SMOOTH));
					System.out.println("Tiempo transcurrido: " + (System.nanoTime() - start) + " nanosegundos");
//				}
			}
	        return false;
		});
	}

	@Override
	public void react() {
		pos = Niveles.cargado().getPos();
		double[] ángulos = Niveles.cargado().getRotación();
		teta_hori = ángulos[0];
		teta_vert = ángulos[1];
		teta_inclin = ángulos[2];
	}

	@Override
	public Pantalla getPantalla() {
		return pantalla;
	}
	
	@Override
	public Image getSalida() {
		return imgSalida.getImage();
	}
}
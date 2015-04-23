package com.stabilise.core;

import static com.badlogic.gdx.Input.Keys;

import com.badlogic.gdx.InputProcessor;
import com.stabilise.core.state.LoadingState;
import com.stabilise.entity.EntityMob;
import com.stabilise.entity.controller.PlayerController;
import com.stabilise.input.Controllable;
import com.stabilise.input.Controller;
import com.stabilise.input.Controller.Control;
import com.stabilise.util.Log;
import com.stabilise.util.Profiler;
import com.stabilise.world.HostWorld;
import com.stabilise.world.World.WorldBundle;
import com.stabilise.world.provider.HostProvider;
import com.stabilise.world.provider.HostProvider.PlayerData;

/**
 * The game itself.
 */
public class Game implements Controllable, InputProcessor {
	
	/** Whether or not the game is currently running. */
	public boolean running = true;
	/** Whether or not the game is currently paused. */
	public boolean paused = false;
	
	private final HostProvider provider;
	/** The game's world instance. */
	public final HostWorld world;
	public final PlayerData playerData;
	public final EntityMob player;
	
	/** The controller. */
	public Controller controller;
	/** The player controller. */
	public PlayerController playerController;
	
	/** The current active menu. */
	//public Menu menu;
	
	/** A reference to the HUD renderer. TODO: Temporary */
	//public HUDRenderer hudRenderer;
	
	/** Whether or not the debug display is active. */
	public boolean debug = false;
	
	/** The game profiler. */
	public Profiler profiler = Application.get().profiler;
	/** The game's logging agent. */
	private final Log log = Log.getAgent("GAME");
	
	
	/**
	 * Creates a new Game instance.
	 * 
	 * @param worldBundle The world and player data.
	 * 
	 * @throws NullPointerException if {@code worldBundle} is {@code null}.
	 */
	public Game(WorldBundle worldBundle) {
		this.provider = worldBundle.provider;
		this.world = worldBundle.world;
		this.playerData = worldBundle.playerData;
		this.player = worldBundle.playerEntity;
		
		log.postInfo("Initiating game...");
		
		controller = new Controller(this);
		
		// TODO: Hardcoding this is poor design and should be changed in the future
		playerController = new PlayerController(player, controller, this);
	}
	
	/**
	 * Updates the game - this should be called by the core game update loop.
	 */
	public void update() {
		/*
		if(world.loading) {
			if(world.regionsLoaded())
				world.loading = false;
			else
				return;
		}
		*/
		
		if(running) {
			try {
				profiler.start("menu"); // root.update.game.menu
				//if(menu != null)
				//	menu.update();
				profiler.next("world"); // root.update.game.world
				if(!paused)
					provider.update();
				profiler.end(); // root.update.game
			} catch(Exception e) {
				log.postSevere("Game encountered error!", e);
				profiler.disable();
				Application a = Application.get();
				a.produceCrashLog();
				//close();			// Simply calling close() makes the game freeze
				a.setState(new LoadingState());
				return;
			}
		}
	}
	
	/**
	 * Renders anything that isn't handled by a renderer.
	 */
	public void render() {
		profiler.start("menu"); // root.render.menu
		//if(menu != null)
		//	menu.render();
		profiler.end(); // root.render
	}
	
	/**
	 * Closes the game - that is, shuts down the world.
	 */
	public void close() {
		running = false;
		//if(menu != null)
		//	menu.unloadResources();
		provider.close();
	}
	
	/**
	 * Gets the game world.
	 */
	public HostWorld getWorld() {
		return world;
	}
	
	/**
	 * Sets the current menu. If the {@code menu} parameter is {@code null},
	 * the current menu, if it exists, will be closed and the game will unpause
	 * as per an invocation of {@link #closeMenu()}, though directly invoking
	 * {@link #closeMenu()} is preferable.
	 * 
	 * @param menu The menu.
	 * @param pause Whether or not to pause the game.
	 */
	/*
	public void setMenu(Menu menu, boolean pause) {
		if(menu == null) {
			closeMenu();
		} else {
			this.menu = menu;
			paused = pause;
		}
	}
	*/
	
	/**
	 * Closes the current menu, if it is non-null. If the game is paused, it
	 * will resume.
	 */
	public void closeMenu() {
		//if(menu != null)
		//	menu.unloadResources();
		//menu = null;
		paused = false;
		controller.input.setInputProcessor(controller);
	}
	
	/**
	 * Opens the pause menu.
	 */
	public void openPauseMenu() {
		//setMenu(new PauseMenu(this), true);
		provider.save();
	}
	
	@Override
	public boolean handleControlPress(Control control) {
		switch(control) {
			case PAUSE:
				openPauseMenu();
				break;
			case DEBUG:
				debug = !debug;
				break;
			default:
				return playerController.handleControlPress(control);
		}
		return true;
	}
	
	@Override
	public boolean handleControlRelease(Control control) {
		return playerController.handleControlRelease(control);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		//hudRenderer.setProfilerSection(keyValue(keycode));
		return false;
	}
	
	@SuppressWarnings("unused")
	private int keyValue(int keycode) {
		switch(keycode) {
			case Keys.NUM_0:
			case Keys.NUMPAD_0:
				return 0;
			case Keys.NUM_1:
			case Keys.NUMPAD_1:
				return 1;
			case Keys.NUM_2:
			case Keys.NUMPAD_2:
				return 2;
			case Keys.NUM_3:
			case Keys.NUMPAD_3:
				return 3;
			case Keys.NUM_4:
			case Keys.NUMPAD_4:
				return 4;
			case Keys.NUM_5:
			case Keys.NUMPAD_5:
				return 5;
			case Keys.NUM_6:
			case Keys.NUMPAD_6:
				return 6;
			case Keys.NUM_7:
			case Keys.NUMPAD_7:
				return 7;
			case Keys.NUM_8:
			case Keys.NUMPAD_8:
				return 8;
			case Keys.NUM_9:
			case Keys.NUMPAD_9:
				return 9;
			default:
				return -1;
		}
	}
	
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}
	
	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		return playerController.touchDown(x, y, pointer, button);
	}
	
	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		return playerController.touchUp(x, y, pointer, button);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return playerController.scrolled(amount);
	}
	
}

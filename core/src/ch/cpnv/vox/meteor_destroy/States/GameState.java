package ch.cpnv.vox.meteor_destroy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

import ch.cpnv.vox.meteor_destroy.Helpers;
import ch.cpnv.vox.meteor_destroy.sprites.Background;
import ch.cpnv.vox.meteor_destroy.sprites.game.Controller;
import ch.cpnv.vox.meteor_destroy.sprites.game.Hud;
import ch.cpnv.vox.meteor_destroy.sprites.game.Meteor;
import ch.cpnv.vox.meteor_destroy.sprites.game.Player;

/**
 * Created by Loic.DESSAULES on 22.05.2017.
 */

public class GameState extends State implements InputProcessor{

    private Background background;
    private Player player;
    private Controller controller;
    private Music audio;
    private Hud hud;
    private Sound greenLaserSound, redLaserSound;
    private static Sound explosionSound, lifeDownSound, deviateSound;
    public static float meteorSpeedY;
    public static ArrayList<Meteor> meteors;
    private long start_time;

    public GameState(GameStateManager gsm) {
        super(gsm);
        // Mandatory to use the InputProcessor on this current state
        Gdx.input.setInputProcessor(this);
        // init Sprites
        background = new Background();
        player = new Player();
        controller =  new Controller();
        hud = new Hud();
        // Audio / sound effects
        initAudio();
        // Built meteors
        start_time =  System.currentTimeMillis();
        meteorSpeedY = Helpers.getHeightAdaptToResolution(8);
        meteors = new ArrayList<>();
        buildMeteor();
    }

    private void initAudio() {
        greenLaserSound = Gdx.audio.newSound(Gdx.files.internal("audio/green_laser.ogg"));
        redLaserSound = Gdx.audio.newSound(Gdx.files.internal("audio/red_laser.ogg"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("audio/explosion.ogg"));
        deviateSound = Gdx.audio.newSound(Gdx.files.internal("audio/deviate.ogg"));
        lifeDownSound = Gdx.audio.newSound(Gdx.files.internal("audio/life_down.ogg"));
        audio = Gdx.audio.newMusic(Gdx.files.internal("audio/game.ogg"));
        audio.play();
        audio.setLooping(true);
    }

    @Override
    public void update(float dt) {
        player.update();
        buildMeteor();
        if(hasMeteor()){
           for(Meteor meteor: meteors){
               meteor.update();
           }
        }
        removeMeteorIfNotAlive();
        hud.update();
        checkGameOver();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        background.draw(sb);
        player.render(sb);
        if(hasMeteor()){
            for(Meteor meteor: meteors){
                meteor.render(sb);
            }
        }
        controller.render(sb);
        hud.render(sb);
        sb.end();
    }

    @Override
    public void dispose() {
        hud.dispose();
        controller.dispose();
        background.getTexture().dispose();
        player.dispose();
        for(Meteor meteor: meteors){
            meteor.dispose();
        }
        audio.dispose();
    }

    // Each 2 seconds, build a new meteor
    // We cannot do this in a thread because we can only create Sprite in the applicationThread who display OpenGL
    private void buildMeteor() {
        long end_time = System.currentTimeMillis();
        if(end_time - start_time >= 2000){
            meteors.add(new Meteor());
            start_time = System.currentTimeMillis();
        }
    }

    private boolean hasMeteor(){
        if(meteors.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    private void removeMeteorIfNotAlive() {
        for (int i=0; i < meteors.size(); i++){
            if(!meteors.get(i).isAlive()){
                // Remove his texture (prevent memory, cpu leaks)
                meteors.get(i).dispose();
                // Remove from the list
                meteors.remove(i);
            }
        }
    }

    public static void playExplosionSound(){
        explosionSound.play();
    }

    public static void playDeviateSound() {
        deviateSound.play();
    }

    public static void playlifeDownSound() {
        lifeDownSound.play();
    }

    public void checkGameOver(){
        if(Player.life <= 0){
            gsm.set(new GameOverState(gsm, Hud.score));
            // dispose all assets elements, to prevent memory leaks
            this.dispose();
        }
    }


    /*-------------------------------------------------------------------*/

    @Override
    public boolean keyDown(int keycode) {
        // Touch the physical back key of the phone
        if(keycode == Input.Keys.BACK){
            // Set the new state in place of the old
            gsm.set(new MenuState(gsm, null));
            // dispose all assets elements, to prevent memory leaks
            this.dispose();
        }
        return false;
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // Left controller detection
        if(controller.getLeftBounds().contains(screenX, screenY)){
            player.setDirection("left");
        }
        // Right controller detection
        if(controller.getRightBounds().contains(screenX, screenY)){
            player.setDirection("right");
        }
        // Shoot controller detection
        if(controller.getShootBounds().contains(screenX, screenY)){
            if(player.getLaserType() == "redLaser"){
                redLaserSound.play();
            }else{
                greenLaserSound.play();
            }
            player.shoot();
        }
        // Change Weapon
        if(controller.getWeaponBounds().contains(screenX, screenY)){
            if(player.getLaserType() == "redLaser"){
                player.setLaserType("greenLaser");
            }else if(player.getLaserType() == "greenLaser"){
                player.setLaserType("redLaser");
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // If we touch up from another location as the ShootButton or change weapon button, we stop the player
        // this way, we can shoot or change Weapon in movement
        if(!controller.getShootBounds().contains(screenX, screenY) &&
            !controller.getWeaponBounds().contains(screenX, screenY)){
            player.setDirection("stop");
        }
        return false;
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
        return false;
    }

}

package ch.cpnv.vox.meteor_destroy.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import ch.cpnv.vox.meteor_destroy.Helpers;
import ch.cpnv.vox.meteor_destroy.sprites.Background;
import ch.cpnv.vox.meteor_destroy.sprites.menu.PlayButton;
import ch.cpnv.vox.meteor_destroy.sprites.menu.Title;

/**
 * Created by Loïc on 19.05.2017.
 */

public class MenuState extends State implements InputProcessor{

    private Background background;
    private Title title;
    private PlayButton btnPlay;
    private Music audio;
    private String vocError;
    private BitmapFont errorFont;
    private GlyphLayout glyphLayout;

    public MenuState(GameStateManager gsm, String vocError) {
        super(gsm);
        this.vocError = vocError;
        // Mandatory to use the InputProcessor on this current state
        Gdx.input.setInputProcessor(this);

        // init Sprite
        background = new Background();
        if(vocError == null){
            btnPlay = new PlayButton();
        }else{
            initFont();
        }
        title = new Title();
        initAudio();
    }

    private void initFont() {
        // get the font which preloaded
        errorFont = Helpers.openSans_100;
        // I used glyphLayout, because with this, we can use the .width attributs, this way it's simple to center the text where we want
        glyphLayout = new GlyphLayout();
        glyphLayout.setText(errorFont, vocError);
    }

    private void initAudio() {
        audio = Gdx.audio.newMusic(Gdx.files.internal("menu/menu.ogg"));
        audio.play();
        audio.setLooping(true);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.begin();
        background.draw(sb);
        title.draw(sb);
        if(vocError == null) {
            btnPlay.draw(sb);
        }else{
            errorFont.draw(sb, vocError, Helpers.getWidthAdaptToResolution(40),Helpers.MOBILE_HEIGHT/2);
        }
        sb.end();
    }

    @Override
    public void dispose() {
        background.getTexture().dispose();
        title.getTexture().dispose();
        if(vocError == null) {
            btnPlay.getTexture().dispose();
        }
        audio.dispose();
    }

    /*-------------------------------------------------------------------*/


    @Override
    public boolean keyDown(int keycode) {
        // Touch the physical back key on the phone
        if(keycode == Input.Keys.BACK){
            Gdx.app.exit();
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
        if (vocError == null){
            // I check if the play button bounds (rectangle) contains our Touch X,Y
            if (btnPlay.getBounds().contains(screenX, screenY)) {
                // Delete the first state and add new one
                audio.stop();
                gsm.set(new GameState(gsm));
                // dispose all assets elements, to prevent memory leaks
                this.dispose();
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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

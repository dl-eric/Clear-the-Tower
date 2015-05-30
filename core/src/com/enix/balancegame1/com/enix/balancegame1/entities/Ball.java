package com.enix.balancegame1.com.enix.balancegame1.entities;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Eric on 5/30/2015.
 */

public class Ball extends InputAdapter implements ContactListener
{
    private Body body;
    private Fixture fixture;
    private final float width, height;
    private Vector2 velocity = new Vector2();
    private float movementForce = 100;
    private Sprite ballSprite;
    private boolean isOnGround;

    public Ball(World world, float x, float y, float width)
    {
        this.width = width;
        height = width;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f);

        //Fixture Definition
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2.5f;
        fixtureDef.friction = 0.25f;
        fixtureDef.restitution = 0;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        ballSprite = new Sprite(new Texture("img/ball.png"));
        ballSprite.setSize(1, 1);
        body.setUserData(ballSprite);

        shape.dispose();
    }

    public void update()
    {
        body.applyForceToCenter(velocity, true);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        body.applyLinearImpulse(0, 65, body.getWorldCenter().x, body.getWorldCenter().y, true);
        return true;
    }

    public Body getBody()
    {
        return body;
    }

    public Fixture getFixture()
    {
        return fixture;
    }

    @Override
    public void beginContact(Contact contact)
    {
        isOnGround = true;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {

    }

    @Override
    public void endContact(Contact contact)
    {
        isOnGround = false;
    }
}
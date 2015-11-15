//package com.stabilise.entity.old;
//
//import com.badlogic.gdx.math.MathUtils;
//import com.stabilise.entity.EntityProjectile;
//import com.stabilise.entity.collision.Hitbox;
//import com.stabilise.entity.effect.EffectFire;
//import com.stabilise.entity.particle.ParticleFlame;
//import com.stabilise.opengl.render.WorldRenderer;
//import com.stabilise.util.Direction;
//import com.stabilise.util.maths.Vec2;
//import com.stabilise.util.shape.AABB;
//import com.stabilise.util.shape.Polygon;
//import com.stabilise.util.shape.Shape;
//import com.stabilise.world.World;
//import com.stabilise.world.AbstractWorld.ParticleSource;
//
///**
// * A Person is a Mob intended to be like a human. The Player is a Person.
// */
//public class EntityPerson extends EntityMob {
//    
//    //--------------------==========--------------------
//    //-----=====Static Constants and Variables=====-----
//    //--------------------==========--------------------
//    
//    // ----------Attacks--------
//    
//    // Side attack (ground)
//    public static final int ATTACK_SIDE_GROUND_DURATION = 40;
//    public static final int ATTACK_SIDE_GROUND_FRAME_2_BEGIN = 16;
//    public static final int ATTACK_SIDE_GROUND_FRAME_3_BEGIN = 20;
//    
//    public static final Shape ATTACK_SIDE_GROUND_HITBOX_1 =
//           Polygon.rectangle(0.53f, 1.11f, 1.82f, 0.18f).rotate(0.099483f);
//    public static final Shape ATTACK_SIDE_GROUND_HITBOX_1_FLIPPED =
//            ATTACK_SIDE_GROUND_HITBOX_1.reflect();
//    public static final Shape ATTACK_SIDE_GROUND_HITBOX_2 =
//            Polygon.rectangle(-1.87f, 1.16f, 2.25f, 0.18f).rotate(0.15708f);
//    public static final Shape ATTACK_SIDE_GROUND_HITBOX_2_FLIPPED =
//            ATTACK_SIDE_GROUND_HITBOX_2.reflect();
//    public static final float ATTACK_SIDE_GROUND_FORCE = 3.2f;
//    
//    // Up attack (ground)
//    public static final int ATTACK_UP_GROUND_DURATION = 40;
//    public static final int ATTACK_UP_GROUND_FRAME_2_BEGIN = 16;
//    public static final AABB ATTACK_UP_GROUND_HITBOX = new AABB(-0.04f, 1.89f, 0.16f, 1.63f);
//    public static final AABB ATTACK_UP_GROUND_HITBOX_FLIPPED =
//            ATTACK_UP_GROUND_HITBOX.reflect();
//    public static final float ATTACK_UP_GROUND_FORCE = 1.1f;
//    public static final int ATTACK_UP_GROUND_HITBOX_DURATION = 14;
//    
//    // Down attack (ground)
//    public static final int ATTACK_DOWN_GROUND_DURATION = 20;
//    public static final int ATTACK_DOWN_GROUND_FRAME_2_BEGIN = 1;
//    public static final Shape ATTACK_DOWN_GROUND_HITBOX =
//            Polygon.rectangle(0.49f, 0.34f, 1.43f, 0.16f).rotate(-0.116937f);
//    public static final Shape ATTACK_DOWN_GROUND_HITBOX_FLIPPED =
//            ATTACK_DOWN_GROUND_HITBOX.reflect();
//    public static final float ATTACK_DOWN_GROUND_FORCE = 1.2f;
//    
//    // Side attack (air)
//    public static final int ATTACK_SIDE_AIR_DURATION = 30;
//    public static final int ATTACK_SIDE_AIR_FRAME_2_BEGIN = 6;
//    public static final Polygon ATTACK_SIDE_AIR_HITBOX_1 =
//            new Polygon(2.72f,1.29f, 0.96f,0.98f, -0.45f,2.56f, 1.29f,2.35f);
//    public static final Shape ATTACK_SIDE_AIR_HITBOX_1_FLIPPED =
//            ATTACK_SIDE_AIR_HITBOX_1.reflect();
//    public static final int ATTACK_SIDE_AIR_FRAME_3_BEGIN = 8;
//    public static final Polygon ATTACK_SIDE_AIR_HITBOX_2 =
//            new Polygon(2.72f,1.29f, 1.44f,-0.41f, 0.45f,0.45f, 0.96f,0.98f);
//    public static final Shape ATTACK_SIDE_AIR_HITBOX_2_FLIPPED =
//            ATTACK_SIDE_AIR_HITBOX_2.reflect();
//    
//    // Up attack (air)
//    public static final int ATTACK_UP_AIR_DURATION = 30;
//    public static final int ATTACK_UP_AIR_FRAME_2_BEGIN = 6;
//    public static final Polygon ATTACK_UP_AIR_HITBOX_1_1 =
//            new Polygon(2.72f,1.51f, 1.22f,-0.45f, 0.8f,0.75f, 0.89f,1.38f);
//    public static final Shape ATTACK_UP_AIR_HITBOX_1_1_FLIPPED =
//            ATTACK_UP_AIR_HITBOX_1_1.reflect();
//    public static final Polygon ATTACK_UP_AIR_HITBOX_1_2 =
//            new Polygon(2.69f,1.95f, 0.84f,1.49f, 0.27f,2.05f, 0.58f,3.33f);
//    public static final Shape ATTACK_UP_AIR_HITBOX_1_2_FLIPPED =
//            ATTACK_UP_AIR_HITBOX_1_2.reflect();
//    public static final int ATTACK_UP_AIR_FRAME_3_BEGIN = 8;
//    public static final Polygon ATTACK_UP_AIR_HITBOX_2 =
//            new Polygon(-0.8f,1.4f, -1.65f,2.4f, 0.6f,3.35f, 0.29f,2.04f);
//    public static final Shape ATTACK_UP_AIR_HITBOX_2_FLIPPED =
//            ATTACK_UP_AIR_HITBOX_2.reflect();
//    
//    // Down attack (air)
//    public static final int ATTACK_DOWN_AIR_DURATION = 30;
//    public static final int ATTACK_DOWN_AIR_FRAME_2_BEGIN = 6;
//    public static final Polygon ATTACK_DOWN_AIR_HITBOX_1_1 =
//            new Polygon(0.78f,0.91f, 1.8f,1.63f, 1.91f,-0.15f, 0.6f,0.65f);
//    public static final Shape ATTACK_DOWN_AIR_HITBOX_1_1_FLIPPED =
//            ATTACK_DOWN_AIR_HITBOX_1_1.reflect();
//    public static final Polygon ATTACK_DOWN_AIR_HITBOX_1_2 =
//            new Polygon(0.6f,0.65f, 1.91f,-0.15f, 0.44f,-0.87f, 0.25f,0.42f);
//    public static final Shape ATTACK_DOWN_AIR_HITBOX_1_2_FLIPPED =
//            ATTACK_DOWN_AIR_HITBOX_1_2.reflect();
//    public static final int ATTACK_DOWN_AIR_FRAME_3_BEGIN = 8;
//    public static final Polygon ATTACK_DOWN_AIR_HITBOX_2 =
//            new Polygon(0.25f,0.42f, 0.44f,-0.87f, -1.47f,0.11f, -0.31f,0.69f);
//    public static final Shape ATTACK_DOWN_AIR_HITBOX_2_FLIPPED =
//            ATTACK_DOWN_AIR_HITBOX_2.reflect();
//    
//    // Specials
//    
//    // Side special (ground)
//    public static final int SPECIAL_SIDE_GROUND_COST_MANA = 50;
//    public static final int SPECIAL_SIDE_GROUND_DURATION = 40;
//    public static final int SPECIAL_SIDE_GROUND_FRAME_2_BEGIN = 16;
//    public static final Vec2 SPECIAL_SIDE_GROUND_ORIGIN = Vec2.immutable(1.09f, 1.25f);
//    
//    // Up special (ground)
//    public static final int SPECIAL_UP_GROUND_COST_MANA = 100;
//    public static final int SPECIAL_UP_GROUND_DURATION = 40;
//    public static final int SPECIAL_UP_GROUND_FRAME_2_BEGIN = 16;
//    public static final Vec2 SPECIAL_UP_GROUND_ORIGIN = Vec2.immutable(0.25f,2.25f);
//    
//    // Down special (ground)
//    public static final int SPECIAL_DOWN_GROUND_COST_MANA = 100;
//    public static final int SPECIAL_DOWN_GROUND_DURATION = 40;
//    public static final int SPECIAL_DOWN_GROUND_FRAME_2_BEGIN = 16;
//    public static final Polygon SPECIAL_DOWN_GROUND_HITBOX_1 =
//            new Polygon(3f,0f, 0f,0f, 0f,2f, 1.5f,2f);
//    public static final Polygon SPECIAL_DOWN_GROUND_HITBOX_2 =
//            new Polygon(0f,0f, -3f,0f, -1.5f,2f, 0f,2f);
//    
//    // Side special (air)
//    public static final int SPECIAL_SIDE_AIR_COST_MANA = 50;
//    public static final int SPECIAL_SIDE_AIR_DURATION = 30;
//    public static final int SPECIAL_SIDE_AIR_FRAME_2_BEGIN = 10;
//    public static final Vec2 SPECIAL_SIDE_AIR_ORIGIN = Vec2.immutable(1.02f,1.33f);
//    
//    // Up special (air)
//    public static final int SPECIAL_UP_AIR_COST_MANA = 100;
//    public static final int SPECIAL_UP_AIR_DURATION = 30;
//    public static final int SPECIAL_UP_AIR_FRAME_2_BEGIN = 10;
//    public static final Vec2 SPECIAL_UP_AIR_ORIGIN = Vec2.immutable(0.27f,2.18f);
//    
//    // Down special (air)
//    public static final int SPECIAL_DOWN_AIR_COST_MANA = 100;
//    public static final int SPECIAL_DOWN_AIR_DURATION = 30;
//    public static final int SPECIAL_DOWN_AIR_FRAME_2_BEGIN = 10;
//    public static final Vec2 SPECIAL_DOWN_AIR_ORIGIN = Vec2.immutable(0.33f,0.36f);
//        
//    //--------------------==========--------------------
//    //-------------=====Member Variables=====-----------
//    //--------------------==========--------------------
//    
//    /** The person's max stamina. */
//    public int maxStamina;
//    /** The person's stamina. */
//    public int stamina;
//    /** The person's max mana. */
//    public int maxMana;
//    /** The person's mana. */
//    public int mana;
//    
//    /** Whether or not the mob's health changed since the last tick. */
//    public boolean healthChanged = false;
//    /** Whether or not the mob's mana changed since the last tick. */
//    public boolean staminaChanged = false;
//    /** Whether or not the mob's mana changed since the last tick. */
//    public boolean manaChanged = false;
//    
//    /** The number of ticks since the mob last lost health. */
//    public int ticksSinceHealthLoss = 0;
//    /** The number of ticks since the mob last lost stamina. */
//    public int ticksSinceStaminaLoss = 0;
//    /** The number of ticks since the mob last lost mana. */
//    public int ticksSinceManaLoss = 0;
//    
//    /** The amount of damage dealt by an attack - for carrying over multiple
//     * frames. */
//    private int damageDealt = 0;
//    
//    private ParticleSource particleSrc;
//    
//    
//    @Override
//    protected AABB getAABB() {
//        return new AABB(-0.4f, 0f, 0.8f, 1.8f);
//        //return new AABB(-0.5f, 0f, 1.0f, 2.0f);
//    }
//    
//    @Override
//    protected void initProperties() {
//        maxHealth = 500;
//        health = 500;
//        maxStamina = 500;
//        stamina = 500;
//        maxMana = 500;
//        mana = 500;
//        
//        jumpVelocity = 16f; // max height = u^2/2g
//        jumpCrouchDuration = 8;
//        //jump = PhysicsUtil.jumpHeightToInitialJumpVelocity(4, gravity);
//        swimAcceleration = 0.08f;
//        acceleration = 1.5f;
//        //airAcceleration = acceleration * 0.15f;
//        airAcceleration = acceleration;
//        maxDx = 15f;
//        
//        state = State.IDLE;
//    }
//    
//    @Override
//    public void update(World world) {
//        super.update(world);
//        
//        if(particleSrc == null)
//            particleSrc = world.getParticleManager().getSource(new ParticleFlame());
//        
//        ticksSinceHealthLoss++;
//        ticksSinceStaminaLoss++;
//        ticksSinceManaLoss++;
//        
//        // Regen health/mana/stamina
//        if(ticksSinceHealthLoss >= 60) {
//            if(ticksSinceHealthLoss >= 300 || ticksSinceHealthLoss % 2 == 0)
//                increaseHealth(1);
//        }
//        
//        if(ticksSinceStaminaLoss >= 60) {
//            if(ticksSinceStaminaLoss >= 300 || ticksSinceStaminaLoss % 2 == 0)
//                increaseStamina(1);
//        }
//        
//        if(ticksSinceManaLoss >= 60) {
//            if(ticksSinceManaLoss >= 300 || ticksSinceManaLoss % 2 == 0)
//                increaseMana(1);
//        }
//        
//        // State-specific scripts
//        switch(state) {
//            case ATTACK_SIDE_GROUND:
//                if(stateTicks == ATTACK_SIDE_GROUND_FRAME_2_BEGIN) {
//                    damageDealt = world.getRnd().nextInt(16) + 5;
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_SIDE_GROUND_HITBOX_1
//                            : ATTACK_SIDE_GROUND_HITBOX_1_FLIPPED, damageDealt);
//                    h.hits = -1;
//                    h.force = ATTACK_SIDE_GROUND_FORCE;
//                    h.fx = facingRight ? 1.0f : -1.0f;
//                    //h.effect = new EffectFire(120);
//                    world.addHitbox(h, x, y);
//                } else if(stateTicks == ATTACK_SIDE_GROUND_FRAME_3_BEGIN) {
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_SIDE_GROUND_HITBOX_2
//                            : ATTACK_SIDE_GROUND_HITBOX_2_FLIPPED, damageDealt);
//                    h.hits = -1;
//                    h.force = ATTACK_SIDE_GROUND_FORCE;
//                    h.fx = facingRight ? -1.0f : 1.0f;
//                    //h.effect = new EffectFire(120);
//                    world.addHitbox(h, x, y);
//                }
//                break;
//            case ATTACK_UP_GROUND:
//                if(stateTicks == ATTACK_UP_GROUND_FRAME_2_BEGIN) {
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_UP_GROUND_HITBOX
//                            : ATTACK_UP_GROUND_HITBOX_FLIPPED,
//                            world.getRnd().nextInt(16) + 20);
//                    h.hits = -1;
//                    h.force = ATTACK_UP_GROUND_FORCE;
//                    h.fy = 1.0f;
//                    h.persistent = true;
//                    h.persistenceTimer = ATTACK_UP_GROUND_HITBOX_DURATION;
//                    world.addHitbox(h, x, y);
//                }
//                break;
//            case ATTACK_DOWN_GROUND:
//                if(stateTicks == ATTACK_DOWN_GROUND_FRAME_2_BEGIN) {
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_DOWN_GROUND_HITBOX
//                            : ATTACK_DOWN_GROUND_HITBOX_FLIPPED,
//                            world.getRnd().nextInt(16) + 5);
//                    h.hits = -1;
//                    h.force = ATTACK_DOWN_GROUND_FORCE;
//                    h.fx = facingRight ? 1.0f : -1.0f;
//                    world.addHitbox(h, x, y);
//                }
//                break;
//            case ATTACK_SIDE_AIR:
//                if(stateTicks == ATTACK_SIDE_AIR_FRAME_2_BEGIN) {
//                    damageDealt = world.getRnd().nextInt(16) + 5;
//                    Hitbox h = new Hitbox(this, facingRight ?
//                            ATTACK_SIDE_AIR_HITBOX_1
//                            : ATTACK_SIDE_AIR_HITBOX_1_FLIPPED, damageDealt);
//                    h.hits = -1;
//                    h.force = 0.3f;
//                    h.fx = facingRight ? 1.0f : -1.0f;
//                    world.addHitbox(h, x, y);
//                } else if(stateTicks == ATTACK_SIDE_AIR_FRAME_3_BEGIN) {
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_SIDE_AIR_HITBOX_2
//                            : ATTACK_SIDE_AIR_HITBOX_2_FLIPPED, damageDealt);
//                    h.hits = -1;
//                    h.force = 0.3f;
//                    h.fx = facingRight ? 1.0f : -1.0f;
//                    world.addHitbox(h, x, y);
//                }
//                break;
//            case ATTACK_UP_AIR:
//                if(stateTicks == ATTACK_UP_AIR_FRAME_2_BEGIN) {
//                    damageDealt = world.getRnd().nextInt(16) + 5;
//                    Hitbox h1 = new Hitbox(this, facingRight
//                            ? ATTACK_UP_AIR_HITBOX_1_1
//                            : ATTACK_UP_AIR_HITBOX_1_1_FLIPPED, damageDealt);
//                    h1.hits = -1;
//                    h1.force = 0.3f;
//                    //h1.fx = facingRight ? 0.86f : -0.85f;
//                    h1.fy = 1.0f;
//                    world.addHitbox(h1, x, y);
//                    
//                    Hitbox h2 = new Hitbox(this, facingRight
//                            ? ATTACK_UP_AIR_HITBOX_1_2
//                            : ATTACK_UP_AIR_HITBOX_1_2_FLIPPED, damageDealt);
//                    h2.hits = -1;
//                    h2.force = 0.3f;
//                    //h2.fx = facingRight ? 0.5f : -0.5f;
//                    h2.fy = 1.0f;
//                    world.addHitbox(h2, x, y);
//                } else if(stateTicks == ATTACK_UP_AIR_FRAME_3_BEGIN) {
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_UP_AIR_HITBOX_2
//                            : ATTACK_UP_AIR_HITBOX_2_FLIPPED, damageDealt);
//                    h.hits = -1;
//                    h.force = 0.3f;
//                    h.fy = 1.0f;
//                    world.addHitbox(h, x, y);
//                }
//                break;
//            case ATTACK_DOWN_AIR:
//                if(stateTicks == ATTACK_DOWN_AIR_FRAME_2_BEGIN) {
//                    damageDealt = world.getRnd().nextInt(16) + 5;
//                    Hitbox h1 = new Hitbox(this, facingRight
//                            ? ATTACK_DOWN_AIR_HITBOX_1_1
//                            : ATTACK_DOWN_AIR_HITBOX_1_1_FLIPPED, damageDealt);
//                    h1.hits = -1;
//                    h1.force = 0.3f;
//                    h1.fy = -1.0f;
//                    world.addHitbox(h1, x, y);
//                    
//                    Hitbox h2 = new Hitbox(this, facingRight
//                            ? ATTACK_DOWN_AIR_HITBOX_1_2
//                            : ATTACK_DOWN_AIR_HITBOX_1_2_FLIPPED, damageDealt);
//                    h2.hits = -1;
//                    h2.force = 0.3f;
//                    h2.fy = -1.0f;
//                    world.addHitbox(h2, x, y);
//                } else if(stateTicks == ATTACK_DOWN_AIR_FRAME_3_BEGIN) {
//                    Hitbox h = new Hitbox(this, facingRight
//                            ? ATTACK_DOWN_AIR_HITBOX_2
//                            : ATTACK_DOWN_AIR_HITBOX_2_FLIPPED, damageDealt);
//                    h.hits = -1;
//                    h.force = 0.3f;
//                    h.fy = -1.0f;
//                    world.addHitbox(h, x, y);
//                }
//                break;
//            case SPECIAL_SIDE_GROUND:
//                if(stateTicks == SPECIAL_SIDE_GROUND_FRAME_2_BEGIN)
//                    fireball(world, SPECIAL_SIDE_GROUND_COST_MANA, SPECIAL_SIDE_GROUND_ORIGIN);
//                break;
//            case SPECIAL_UP_GROUND:
//                if(stateTicks == SPECIAL_UP_GROUND_FRAME_2_BEGIN)
//                    fireballStorm(world, SPECIAL_UP_GROUND_COST_MANA,
//                            SPECIAL_UP_GROUND_ORIGIN);
//                break;
//            case SPECIAL_DOWN_GROUND:
//                if(stateTicks == SPECIAL_DOWN_GROUND_FRAME_2_BEGIN) {
//                    if(useMana(SPECIAL_DOWN_GROUND_COST_MANA)) {
//                        Hitbox h1 = new Hitbox(this, SPECIAL_DOWN_GROUND_HITBOX_1,
//                                world.getRnd().nextInt(16)+5);
//                        h1.hits = -1;
//                        h1.force = 0.3f;
//                        h1.fx = 0.5f;
//                        h1.fy = 0.7f;
//                        h1.effect = new EffectFire(300);
//                        world.addHitbox(h1, x, y);
//                        
//                        Hitbox h2 = new Hitbox(this, SPECIAL_DOWN_GROUND_HITBOX_2,
//                                world.getRnd().nextInt(16)+5);
//                        h2.hits = -1;
//                        h2.force = 0.3f;
//                        h2.fx = -0.5f;
//                        h2.fy = 0.7f;
//                        h2.effect = new EffectFire(300);
//                        world.addHitbox(h2, x, y);
//                        
//                        particleSrc.createBurst(300, x, y, 0.001f, 0.10f, 0, (float)Math.PI);
//                    } else {
//                        particleSrc.createBurst(100, x, y, 0.0001f, 0.08f, 0, (float)Math.PI);
//                    }
//                }
//                break;
//            case SPECIAL_SIDE_AIR:
//                if(stateTicks == SPECIAL_SIDE_AIR_FRAME_2_BEGIN)
//                    fireball(world, SPECIAL_SIDE_AIR_COST_MANA, SPECIAL_SIDE_AIR_ORIGIN);
//                break;
//            case SPECIAL_UP_AIR:
//                if(stateTicks == SPECIAL_UP_AIR_FRAME_2_BEGIN)
//                    fireballStorm(world, SPECIAL_UP_AIR_COST_MANA, SPECIAL_UP_AIR_ORIGIN);
//                break;
//            case SPECIAL_DOWN_AIR:
//                if(stateTicks == SPECIAL_DOWN_AIR_FRAME_2_BEGIN) {
//                    if(useMana(SPECIAL_DOWN_AIR_COST_MANA)) {
//                        EntityBigFireball f = new EntityBigFireball(world, this);
//                        f.x = x + (facingRight ? SPECIAL_DOWN_AIR_ORIGIN.x()
//                                : -SPECIAL_DOWN_AIR_ORIGIN.x());
//                        f.y = y + SPECIAL_DOWN_AIR_ORIGIN.y();
//                        f.dy = Math.min(0f, dy + world.getRnd().nextFloat() * 3.0f - 10f);
//                        world.addEntity(f);
//                    } else {
//                        double minAngle, maxAngle, px;
//                        
//                        minAngle = -1.0D * Math.PI / 3.0D;
//                        maxAngle = -2.0D * Math.PI / 3.0D;
//                        
//                        if(facingRight) {
//                            px = x + SPECIAL_DOWN_AIR_ORIGIN.x();
//                        } else {
//                            px = x - SPECIAL_DOWN_AIR_ORIGIN.x();
//                        }
//                        
//                        particleSrc.createBurst(6, px, y + SPECIAL_DOWN_AIR_ORIGIN.y(),
//                                0.03f, 0.08f, (float)minAngle, (float)maxAngle);
//                    }
//                }
//                break;
//            default:
//                // nothing here
//                break;
//        }
//    }
//    
//    private void fireball(World world, int manaCost, Vec2 originPoint) {
//        if(useMana(manaCost)) {
//            EntityFireball e = new EntityFireball(world, this,
//                    world.getRnd().nextInt(3) + 5);
//            
//            if(facingRight) {
//                e.x = x + originPoint.x();
//                e.dx = 30f + world.getRnd().nextFloat() * 10f;
//            } else {
//                e.x = x - originPoint.x();
//                e.dx = -30f - world.getRnd().nextFloat() * 10f;
//            }
//            
//            e.y = y + originPoint.y();
//            e.dy = 1.0f + world.getRnd().nextFloat() * 1.8f;
//            
//            e.facingRight = facingRight;
//            
//            world.addEntity(e);
//        } else {
//            double minAngle, maxAngle, px;
//            
//            if(facingRight) {
//                px = x + originPoint.x();
//                minAngle = -Math.PI / 6.0D;
//                maxAngle = Math.PI / 6.0D;
//            } else {
//                px = x - originPoint.x();
//                minAngle = Math.PI * 5.0D / 6.0D;
//                maxAngle = Math.PI * 7.0D / 7.0D;
//            }
//            
//            particleSrc.createBurst(6, px, y + originPoint.y(),
//                    0.03f, 0.08f, (float)minAngle, (float)maxAngle);
//        }
//    }
//    
//    /**
//     * Creates a fireball storm.
//     * 
//     * @param manaCost The mana cost of the storm.
//     * @param originPoint The point from which the storm is to originate.
//     */
//    private void fireballStorm(World world, int manaCost, Vec2 originPoint) {
//        double px = facingRight ? x + originPoint.x() : x - originPoint.x();
//        
//        if(useMana(manaCost)) {
//            int max = 30 + world.getRnd().nextInt(11);
//            for(int i = 0; i < max; i++) {
//                float angle = (world.getRnd().nextFloat() * 0.47f + 0.03f) * MathUtils.PI;
//                float velocity = 18.5f + world.getRnd().nextFloat() * 6.5f;
//                boolean right = world.getRnd().nextBoolean();
//                EntityProjectile e = new EntityFireball(world, this, world.getRnd().nextInt(3) + 5);
//                e.dx = right ? MathUtils.cos(angle) * velocity : -MathUtils.cos(angle) * velocity;
//                e.dy = MathUtils.sin(angle)*velocity;
//                e.facingRight = right;
//                world.addEntity(e, px, y + originPoint.y());
//            }
//        } else {
//            particleSrc.createBurst(6, px, y + originPoint.y(),
//                    0.03f, 0.08f, MathUtils.PI / 3f, MathUtils.PI * 0.6666f);
//        }
//    }
//    
//    @Override
//    protected void onVerticalCollision() {
//        if(dy < 0 && !wasOnGround && state.priority != StatePriority.UNOVERRIDEABLE) {
//            if(dy < -jumpVelocity) {
//                setState(State.LAND_CROUCH, false, 20);        // TODO: temporary constant duration
//            } else {
//                switch(state) {
//                    case ATTACK_SIDE_AIR:
//                        if(ATTACK_SIDE_AIR_DURATION - stateTicks > 10)
//                            setState(State.LAND_CROUCH, false, 20);
//                        else
//                            stateLockDuration = 0;
//                        break;
//                    case ATTACK_UP_AIR:
//                        if(ATTACK_UP_AIR_DURATION - stateTicks > 10)
//                            setState(State.LAND_CROUCH, false, 20);
//                        else
//                            stateLockDuration = 0;
//                        break;
//                    case ATTACK_DOWN_AIR:
//                        if(ATTACK_DOWN_AIR_DURATION - stateTicks > 10)
//                            setState(State.LAND_CROUCH, false, 20);
//                        else
//                            stateLockDuration = 0;
//                        break;
//                    default:
//                        stateLockDuration = 0;
//                        break;
//                }
//            }
//        }
//    }
//    
//    @Override
//    public void attack(Direction direction) {
//        if(!state.canAct)
//            return;
//        
//        if(onGround) {
//            switch(direction) {
//                case LEFT:
//                case RIGHT:
//                    setState(State.ATTACK_SIDE_GROUND, true, ATTACK_SIDE_GROUND_DURATION);
//                    setFacingRight(direction.hasRight());
//                    break;
//                case UP:
//                    setState(State.ATTACK_UP_GROUND, true, ATTACK_UP_GROUND_DURATION);
//                    break;
//                case DOWN:
//                    setState(State.ATTACK_DOWN_GROUND, true, ATTACK_DOWN_GROUND_DURATION);
//                    break;
//                default:
//                    // nothing
//                    break;
//            }
//        } else {
//            switch(direction) {
//                case LEFT:
//                case RIGHT:
//                    setState(State.ATTACK_SIDE_AIR, true, ATTACK_SIDE_AIR_DURATION);
//                    setFacingRight(direction.hasRight());
//                    break;
//                case UP:
//                    setState(State.ATTACK_UP_AIR, true, ATTACK_UP_AIR_DURATION);
//                    break;
//                case DOWN:
//                    setState(State.ATTACK_DOWN_AIR, true, ATTACK_DOWN_AIR_DURATION);
//                    break;
//                default:
//                    // nothing
//                    break;
//            }
//        }
//    }
//    
//    @Override
//    public void specialAttack(Direction direction) {
//        if(!state.canAct)
//            return;
//        
//        if(onGround) {
//            switch(direction) {
//                case LEFT:
//                case RIGHT:
//                    setState(State.SPECIAL_SIDE_GROUND, true, SPECIAL_SIDE_GROUND_DURATION);
//                    setFacingRight(direction.hasRight());
//                    break;
//                case UP:
//                    setState(State.SPECIAL_UP_GROUND, true, SPECIAL_UP_GROUND_DURATION);
//                    break;
//                case DOWN:
//                    setState(State.SPECIAL_DOWN_GROUND, true, SPECIAL_DOWN_GROUND_DURATION);
//                    break;
//                default:
//                    // nothing
//                    break;
//            }
//        } else {
//            switch(direction) {
//                case LEFT:
//                case RIGHT:
//                    setState(State.SPECIAL_SIDE_AIR, true, SPECIAL_SIDE_AIR_DURATION);
//                    setFacingRight(direction.hasRight());
//                    break;
//                case UP:
//                    setState(State.SPECIAL_UP_AIR, true, SPECIAL_UP_AIR_DURATION);
//                    break;
//                case DOWN:
//                    setState(State.SPECIAL_DOWN_AIR, true, SPECIAL_DOWN_AIR_DURATION);
//                    break;
//                default:
//                    // nothing
//                    break;
//            }
//        }
//    }
//    
//    @Override
//    public void restore() {
//        increaseHealth(maxHealth);
//        increaseStamina(maxStamina);
//        increaseMana(maxMana);
//    }
//    
//    /**
//     * Attempts to consume some of the person's stamina.
//     * 
//     * @param stamina The amount of stamina to use.
//     * 
//     * @return {@code true} if the person had an amount of stamina greater
//     * than or equal to the given value; {@code false} otherwise.
//     */
//    @SuppressWarnings("unused")
//    private boolean useStamina(int stamina) {
//        ticksSinceStaminaLoss = 0;
//        
//        if(this.stamina < stamina)
//            return false;
//        
//        staminaChanged = true;
//        this.stamina -= stamina;
//        
//        return true;
//    }
//    
//    /**
//     * Attempts to consume some of the person's mana.
//     * 
//     * @param mana The amount of mana to use.
//     * 
//     * @return {@code true} if the person had an amount of mana greater than
//     * or equal to the given value; {@code false} otherwise.
//     */
//    private boolean useMana(int mana) {
//        ticksSinceManaLoss = 0;
//        
//        if(this.mana < mana)
//            return false;
//        
//        manaChanged = true;
//        this.mana -= mana;
//        
//        return true;
//    }
//    
//    /**
//     * Increases the person's health.
//     * 
//     * @param amount The amount by which to increase the person's health.
//     */
//    private void increaseHealth(int amount) {
//        if(health == maxHealth)
//            return;
//        
//        health += amount;
//        if(health > maxHealth)
//            health = maxHealth;
//        
//        healthChanged = true;
//    }
//    
//    /**
//     * Increases the person's stamina.
//     * 
//     * @param amount The amount by which to increase the person's stamina.
//     */
//    private void increaseStamina(int amount) {
//        if(stamina == maxStamina)
//            return;
//        
//        stamina += amount;
//        if(stamina > maxStamina)
//            stamina = maxStamina;
//        
//        staminaChanged = true;
//    }
//    
//    /**
//     * Increases the person's mana.
//     * 
//     * @param amount The amount by which to increase the person's mana.
//     */
//    private void increaseMana(int amount) {
//        if(mana == maxMana)
//            return;
//        
//        mana += amount;
//        if(mana > maxMana)
//            mana = maxMana;
//        
//        manaChanged = true;
//    }
//    
//    @Override
//    public void render(WorldRenderer renderer) {
//        renderer.renderPerson(this);
//    }
//    
//}
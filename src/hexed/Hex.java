package hexed;

import arc.math.geom.*;
import arc.struct.IntIntMap;
import arc.util.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import java.util.*;

import static mindustry.Vars.*;

public class Hex{
    private float[] progress = new float[256];

    public final static int size = 516;
    public final static int diameter = 74;
    public final static int radius = diameter / 2;
    public final static int spacing = 78;

    public final int id;
    public final int x, y;
    public final float wx, wy;
    public final float rad = radius * tilesize;

    public @Nullable Team controller;
    public CooldownTimer spawnTime = new CooldownTimer((long) HexedMod.spawnDelay);

    public Hex(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
        wx = x * tilesize;
        wy = y * tilesize;
    }

    public void updateController(IntIntMap hexes_per_team){
        controller = findController(hexes_per_team);
    }

    public float getProgressPercent(Team team){
        return progress[team.id] / HexedMod.itemRequirement * 100;
    }

    public float getProgress(Team team){
        return progress[team.id];
    }

    public boolean hasCore(){
        return world.tile(x, y).team() != Team.derelict && world.tile(x, y).block() instanceof CoreBlock;
    }

    public @Nullable Team findController(IntIntMap hexes_per_team){
        if(hasCore()){
            return world.tile(x, y).team();
        }

        Arrays.fill(progress, 0);
//        Groups.unit.intersect(wx - rad, wy - rad, rad*2, rad*2).each(e -> {
//            if(contains(e.x, e.y)){
//                progress[e.team.id] += e.health / 10f;
//            }
//        });

        for(int cx = x - radius; cx < x + radius; cx++){
            for(int cy = y - radius; cy < y + radius; cy++){
                Tile tile = world.tile(cx, cy);
                if(tile != null && tile.synthetic() && contains(tile) && tile.block().requirements != null){
                    for(ItemStack stack : tile.block().requirements){
                        // progress[tile.team().id] += stack.amount * stack.item.cost;
                        float amt = (float) (stack.amount * 15) /(15f+2.5f*hexes_per_team.get(tile.team().id,0));
                        progress[tile.team().id] += amt;
                    }
                }
            }
        }

        TeamData data = state.teams.getActive().max(t -> progress[t.team.id]);
        if(data != null && data.team != Team.derelict && progress[data.team.id] >= HexedMod.itemRequirement){
            return data.team;
        }
        return null;
    }

    public boolean contains(float x, float y){
        return Intersector.isInsideHexagon(wx, wy, rad * 2, x, y);
    }

    public boolean contains(Tile tile){
        return contains(tile.worldx(), tile.worldy());
    }
}

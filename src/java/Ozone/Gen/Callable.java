/*
1:07:16 pm March 23, 2021
Source: src/java/Ozone/Net/Callable.java
Generated for Mindustry: v126.1
*/
package Ozone.Gen;

import Ozone.Net.LoggableNet;
import arc.util.io.ReusableByteOutStream;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.net.Net;

import java.io.DataOutputStream;

public class Callable {
    
    private static StaticNet staticNet = new StaticNet();
    protected final ReusableByteOutStream OUT = new ReusableByteOutStream(8192);
    protected final Writes WRITE = new Writes(new DataOutputStream(OUT));
    private Net net, original;
    private Net.NetProvider provider;
    
    public Callable(Net net) {
        this.net = net;
    }
    
    public Callable(Net.NetProvider provider) {
        this.provider = provider;
    }
    
    public void pre() {
        original = Vars.net;
        staticNet.setNet(net).setProvider(provider);
        Vars.net = staticNet;
    }
    
    public void post() {
        Vars.net = original;
        original = null;
    }
    
    public void base() {
        pre();
    }
    
    public void label(java.lang.String message, float duration, float worldx, float worldy) {
        pre();
        mindustry.gen.Call.label(message, duration, worldx, worldy);
        post();
    }
    
    public void label(mindustry.net.NetConnection playerConnection, java.lang.String message, float duration, float worldx, float worldy) {
        pre();
        mindustry.gen.Call.label(playerConnection, message, duration, worldx, worldy);
        post();
    }
    
    public void connect(mindustry.net.NetConnection playerConnection, java.lang.String ip, int port) {
        pre();
        mindustry.gen.Call.connect(playerConnection, ip, port);
        post();
    }
    
    public void updateGameOver(mindustry.game.Team winner) {
        pre();
        mindustry.gen.Call.updateGameOver(winner);
        post();
    }
    
    public void unitDestroy(int uid) {
        pre();
        mindustry.gen.Call.unitDestroy(uid);
        post();
    }
    
    public void unitDespawn(mindustry.gen.Unit unit) {
        pre();
        mindustry.gen.Call.unitDespawn(unit);
        post();
    }
    
    public void unitDeath(int uid) {
        pre();
        mindustry.gen.Call.unitDeath(uid);
        post();
    }
    
    public void unitControl(mindustry.gen.Player player, mindustry.gen.Unit unit) {
        pre();
        mindustry.gen.Call.unitControl(player, unit);
        post();
    }
    
    public void unitCommand(mindustry.gen.Player player) {
        pre();
        mindustry.gen.Call.unitCommand(player);
        post();
    }
    
    public void unitClear(mindustry.gen.Player player) {
        pre();
        mindustry.gen.Call.unitClear(player);
        post();
    }
    
    public void unitCapDeath(mindustry.gen.Unit unit) {
        pre();
        mindustry.gen.Call.unitCapDeath(unit);
        post();
    }
    
    public void unitBlockSpawn(mindustry.world.Tile tile) {
        pre();
        mindustry.gen.Call.unitBlockSpawn(tile);
        post();
    }
    
    public void transferItemToUnit(mindustry.type.Item item, float x, float y, mindustry.gen.Itemsc to) {
        pre();
        mindustry.gen.Call.transferItemToUnit(item, x, y, to);
        post();
    }
    
    public void transferItemTo(mindustry.gen.Unit unit, mindustry.type.Item item, int amount, float x, float y, mindustry.gen.Building build) {
        pre();
        mindustry.gen.Call.transferItemTo(unit, item, amount, x, y, build);
        post();
    }
    
    public void transferItemEffect(mindustry.type.Item item, float x, float y, mindustry.gen.Itemsc to) {
        pre();
        mindustry.gen.Call.transferItemEffect(item, x, y, to);
        post();
    }
    
    public void transferInventory(mindustry.gen.Player player, mindustry.gen.Building build) {
        pre();
        mindustry.gen.Call.transferInventory(player, build);
        post();
    }
    
    public void tileTap(mindustry.gen.Player player, mindustry.world.Tile tile) {
        pre();
        mindustry.gen.Call.tileTap(player, tile);
        post();
    }
    
    public void tileDestroyed(mindustry.gen.Building build) {
        pre();
        mindustry.gen.Call.tileDestroyed(build);
        post();
    }
    
    public void tileDamage(mindustry.gen.Building build, float health) {
        pre();
        mindustry.gen.Call.tileDamage(build, health);
        post();
    }
    
    public void tileConfig(mindustry.gen.Player player, mindustry.gen.Building build, java.lang.Object value) {
        pre();
        mindustry.gen.Call.tileConfig(player, build, value);
        post();
    }
    
    public void takeItems(mindustry.gen.Building build, mindustry.type.Item item, int amount, mindustry.gen.Unit to) {
        pre();
        mindustry.gen.Call.takeItems(build, item, amount, to);
        post();
    }
    
    public void spawnEffect(float x, float y, float rotation, mindustry.type.UnitType u) {
        pre();
        mindustry.gen.Call.spawnEffect(x, y, rotation, u);
        post();
    }
    
    public void setTile(mindustry.world.Tile tile, mindustry.world.Block block, mindustry.game.Team team, int rotation) {
        pre();
        mindustry.gen.Call.setTile(tile, block, team, rotation);
        post();
    }
    
    public void setTeam(mindustry.gen.Building build, mindustry.game.Team team) {
        pre();
        mindustry.gen.Call.setTeam(build, team);
        post();
    }
    
    public void setPlayerTeamEditor(mindustry.gen.Player player, mindustry.game.Team team) {
        pre();
        mindustry.gen.Call.setPlayerTeamEditor(player, team);
        post();
    }
    
    public void setItem(mindustry.gen.Building build, mindustry.type.Item item, int amount) {
        pre();
        mindustry.gen.Call.setItem(build, item, amount);
        post();
    }
    
    public void setFloor(mindustry.world.Tile tile, mindustry.world.Block floor, mindustry.world.Block overlay) {
        pre();
        mindustry.gen.Call.setFloor(tile, floor, overlay);
        post();
    }
    
    public void sendMessage(java.lang.String message, java.lang.String sender, mindustry.gen.Player playersender) {
        pre();
        mindustry.gen.Call.sendMessage(message, sender, playersender);
        post();
    }
    
    public void sendMessage(mindustry.net.NetConnection playerConnection, java.lang.String message, java.lang.String sender, mindustry.gen.Player playersender) {
        pre();
        mindustry.gen.Call.sendMessage(playerConnection, message, sender, playersender);
        post();
    }
    
    public void sendMessage(java.lang.String message) {
        pre();
        mindustry.gen.Call.sendMessage(message);
        post();
    }
    
    public void sendChatMessage(java.lang.String message) {
        pre();
        mindustry.gen.Call.sendChatMessage(message);
        post();
    }
    
    public void sectorCapture() {
        pre();
        mindustry.gen.Call.sectorCapture();
        post();
    }
    
    public void rotateBlock(mindustry.gen.Player player, mindustry.gen.Building build, boolean direction) {
        pre();
        mindustry.gen.Call.rotateBlock(player, build, direction);
        post();
    }
    
    public void requestUnitPayload(mindustry.gen.Player player, mindustry.gen.Unit target) {
        pre();
        mindustry.gen.Call.requestUnitPayload(player, target);
        post();
    }
    
    public void requestItem(mindustry.gen.Player player, mindustry.gen.Building build, mindustry.type.Item item, int amount) {
        pre();
        mindustry.gen.Call.requestItem(player, build, item, amount);
        post();
    }
    
    public void requestDropPayload(mindustry.gen.Player player, float x, float y) {
        pre();
        mindustry.gen.Call.requestDropPayload(player, x, y);
        post();
    }
    
    public void requestBuildPayload(mindustry.gen.Player player, mindustry.gen.Building build) {
        pre();
        mindustry.gen.Call.requestBuildPayload(player, build);
        post();
    }
    
    public void removeTile(mindustry.world.Tile tile) {
        pre();
        mindustry.gen.Call.removeTile(tile);
        post();
    }
    
    public void playerSpawn(mindustry.world.Tile tile, mindustry.gen.Player player) {
        pre();
        mindustry.gen.Call.playerSpawn(tile, player);
        post();
    }
    
    public void pickedUnitPayload(mindustry.gen.Unit unit, mindustry.gen.Unit target) {
        pre();
        mindustry.gen.Call.pickedUnitPayload(unit, target);
        post();
    }
    
    public void pickedBuildPayload(mindustry.gen.Unit unit, mindustry.gen.Building build, boolean onGround) {
        pre();
        mindustry.gen.Call.pickedBuildPayload(unit, build, onGround);
        post();
    }
    
    public void payloadDropped(mindustry.gen.Unit unit, float x, float y) {
        pre();
        mindustry.gen.Call.payloadDropped(unit, x, y);
        post();
    }
    
    public void gameOver(mindustry.game.Team winner) {
        pre();
        mindustry.gen.Call.gameOver(winner);
        post();
    }
    
    public void dropItem(float angle) {
        pre();
        mindustry.gen.Call.dropItem(angle);
        post();
    }
    
    public void deconstructFinish(mindustry.world.Tile tile, mindustry.world.Block block, mindustry.gen.Unit builder) {
        pre();
        mindustry.gen.Call.deconstructFinish(tile, block, builder);
        post();
    }
    
    public void createWeather(mindustry.type.Weather weather, float intensity, float duration, float windX, float windY) {
        pre();
        mindustry.gen.Call.createWeather(weather, intensity, duration, windX, windY);
        post();
    }
    
    public void createBullet(mindustry.entities.bullet.BulletType type, mindustry.game.Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl) {
        pre();
        mindustry.gen.Call.createBullet(type, team, x, y, angle, damage, velocityScl, lifetimeScl);
        post();
    }
    
    public void constructFinish(mindustry.world.Tile tile, mindustry.world.Block block, mindustry.gen.Unit builder, byte rotation, mindustry.game.Team team, java.lang.Object config) {
        pre();
        mindustry.gen.Call.constructFinish(tile, block, builder, rotation, team, config);
        post();
    }
    
    public void clearItems(mindustry.gen.Building build) {
        pre();
        mindustry.gen.Call.clearItems(build);
        post();
    }
    
    public void beginPlace(mindustry.gen.Unit unit, mindustry.world.Block result, mindustry.game.Team team, int x, int y, int rotation) {
        pre();
        mindustry.gen.Call.beginPlace(unit, result, team, x, y, rotation);
        post();
    }
    
    public void beginBreak(mindustry.gen.Unit unit, mindustry.game.Team team, int x, int y) {
        pre();
        mindustry.gen.Call.beginBreak(unit, team, x, y);
        post();
    }
    
    public void adminRequest(mindustry.gen.Player other, mindustry.net.Packets.AdminAction action) {
        pre();
        mindustry.gen.Call.adminRequest(other, action);
        post();
    }
    
    public void worldDataBegin(mindustry.net.NetConnection playerConnection) {
        pre();
        mindustry.gen.Call.worldDataBegin(playerConnection);
        post();
    }
    
    public void worldDataBegin() {
        pre();
        mindustry.gen.Call.worldDataBegin();
        post();
    }
    
    public void warningToast(mindustry.net.NetConnection playerConnection, int unicode, java.lang.String text) {
        pre();
        mindustry.gen.Call.warningToast(playerConnection, unicode, text);
        post();
    }
    
    public void warningToast(int unicode, java.lang.String text) {
        pre();
        mindustry.gen.Call.warningToast(unicode, text);
        post();
    }
    
    public void traceInfo(mindustry.net.NetConnection playerConnection, mindustry.gen.Player player, mindustry.net.Administration.TraceInfo info) {
        pre();
        mindustry.gen.Call.traceInfo(playerConnection, player, info);
        post();
    }
    
    public void stateSnapshot(mindustry.net.NetConnection playerConnection, float waveTime, int wave, int enemies, boolean paused, boolean gameOver, int timeData, short coreDataLen, byte[] coreData) {
        pre();
        mindustry.gen.Call.stateSnapshot(playerConnection, waveTime, wave, enemies, paused, gameOver, timeData, coreDataLen, coreData);
        post();
    }
    
    public void setRules(mindustry.game.Rules rules) {
        pre();
        mindustry.gen.Call.setRules(rules);
        post();
    }
    
    public void setRules(mindustry.net.NetConnection playerConnection, mindustry.game.Rules rules) {
        pre();
        mindustry.gen.Call.setRules(playerConnection, rules);
        post();
    }
    
    public void setPosition(mindustry.net.NetConnection playerConnection, float x, float y) {
        pre();
        mindustry.gen.Call.setPosition(playerConnection, x, y);
        post();
    }
    
    public void setHudTextReliable(mindustry.net.NetConnection playerConnection, java.lang.String message) {
        pre();
        mindustry.gen.Call.setHudTextReliable(playerConnection, message);
        post();
    }
    
    public void setHudTextReliable(java.lang.String message) {
        pre();
        mindustry.gen.Call.setHudTextReliable(message);
        post();
    }
    
    public void setHudText(java.lang.String message) {
        pre();
        mindustry.gen.Call.setHudText(message);
        post();
    }
    
    public void setHudText(mindustry.net.NetConnection playerConnection, java.lang.String message) {
        pre();
        mindustry.gen.Call.setHudText(playerConnection, message);
        post();
    }
    
    public void serverPacketUnreliable(java.lang.String type, java.lang.String contents) {
        pre();
        mindustry.gen.Call.serverPacketUnreliable(type, contents);
        post();
    }
    
    public void serverPacketReliable(java.lang.String type, java.lang.String contents) {
        pre();
        mindustry.gen.Call.serverPacketReliable(type, contents);
        post();
    }
    
    public void sectorProduced(int[] amounts) {
        pre();
        mindustry.gen.Call.sectorProduced(amounts);
        post();
    }
    
    public void researched(mindustry.ctype.Content content) {
        pre();
        mindustry.gen.Call.researched(content);
        post();
    }
    
    public void removeQueueBlock(mindustry.net.NetConnection playerConnection, int x, int y, boolean breaking) {
        pre();
        mindustry.gen.Call.removeQueueBlock(playerConnection, x, y, breaking);
        post();
    }
    
    public void playerDisconnect(int playerid) {
        pre();
        mindustry.gen.Call.playerDisconnect(playerid);
        post();
    }
    
    public void pingResponse(mindustry.net.NetConnection playerConnection, long time) {
        pre();
        mindustry.gen.Call.pingResponse(playerConnection, time);
        post();
    }
    
    public void ping(long time) {
        pre();
        mindustry.gen.Call.ping(time);
        post();
    }
    
    public void kick(mindustry.net.NetConnection playerConnection, java.lang.String reason) {
        pre();
        mindustry.gen.Call.kick(playerConnection, reason);
        post();
    }
    
    public void kick(mindustry.net.NetConnection playerConnection, mindustry.net.Packets.KickReason reason) {
        pre();
        mindustry.gen.Call.kick(playerConnection, reason);
        post();
    }
    
    public void infoToast(mindustry.net.NetConnection playerConnection, java.lang.String message, float duration) {
        pre();
        mindustry.gen.Call.infoToast(playerConnection, message, duration);
        post();
    }
    
    public void infoToast(java.lang.String message, float duration) {
        pre();
        mindustry.gen.Call.infoToast(message, duration);
        post();
    }
    
    public void infoPopup(java.lang.String message, float duration, int align, int top, int left, int bottom, int right) {
        pre();
        mindustry.gen.Call.infoPopup(message, duration, align, top, left, bottom, right);
        post();
    }
    
    public void infoPopup(mindustry.net.NetConnection playerConnection, java.lang.String message, float duration, int align, int top, int left, int bottom, int right) {
        pre();
        mindustry.gen.Call.infoPopup(playerConnection, message, duration, align, top, left, bottom, right);
        post();
    }
    
    public void infoMessage(mindustry.net.NetConnection playerConnection, java.lang.String message) {
        pre();
        mindustry.gen.Call.infoMessage(playerConnection, message);
        post();
    }
    
    public void infoMessage(java.lang.String message) {
        pre();
        mindustry.gen.Call.infoMessage(message);
        post();
    }
    
    public void hideHudText() {
        pre();
        mindustry.gen.Call.hideHudText();
        post();
    }
    
    public void hideHudText(mindustry.net.NetConnection playerConnection) {
        pre();
        mindustry.gen.Call.hideHudText(playerConnection);
        post();
    }
    
    public void entitySnapshot(mindustry.net.NetConnection playerConnection, short amount, short dataLen, byte[] data) {
        pre();
        mindustry.gen.Call.entitySnapshot(playerConnection, amount, dataLen, data);
        post();
    }
    
    public void effectReliable(mindustry.net.NetConnection playerConnection, mindustry.entities.Effect effect, float x, float y, float rotation, arc.graphics.Color color) {
        pre();
        mindustry.gen.Call.effectReliable(playerConnection, effect, x, y, rotation, color);
        post();
    }
    
    public void effectReliable(mindustry.entities.Effect effect, float x, float y, float rotation, arc.graphics.Color color) {
        pre();
        mindustry.gen.Call.effectReliable(effect, x, y, rotation, color);
        post();
    }
    
    public void effect(mindustry.net.NetConnection playerConnection, mindustry.entities.Effect effect, float x, float y, float rotation, arc.graphics.Color color) {
        pre();
        mindustry.gen.Call.effect(playerConnection, effect, x, y, rotation, color);
        post();
    }
    
    public void effect(mindustry.entities.Effect effect, float x, float y, float rotation, arc.graphics.Color color) {
        pre();
        mindustry.gen.Call.effect(effect, x, y, rotation, color);
        post();
    }
    
    public void connectConfirm() {
        pre();
        mindustry.gen.Call.connectConfirm();
        post();
    }
    
    public void clientSnapshot(int snapshotID, int unitID, boolean dead, float x, float y, float pointerX, float pointerY, float rotation, float baseRotation, float xVelocity, float yVelocity, mindustry.world.Tile mining, boolean boosting, boolean shooting, boolean chatting, boolean building, mindustry.entities.units.BuildPlan[] requests, float viewX, float viewY, float viewWidth, float viewHeight) {
        pre();
        mindustry.gen.Call.clientSnapshot(snapshotID, unitID, dead, x, y, pointerX, pointerY, rotation, baseRotation, xVelocity, yVelocity, mining, boosting, shooting, chatting, building, requests, viewX, viewY, viewWidth, viewHeight);
        post();
    }
    
    public void clientPacketUnreliable(mindustry.net.NetConnection playerConnection, java.lang.String type, java.lang.String contents) {
        pre();
        mindustry.gen.Call.clientPacketUnreliable(playerConnection, type, contents);
        post();
    }
    
    public void clientPacketUnreliable(java.lang.String type, java.lang.String contents) {
        pre();
        mindustry.gen.Call.clientPacketUnreliable(type, contents);
        post();
    }
    
    public void clientPacketReliable(mindustry.net.NetConnection playerConnection, java.lang.String type, java.lang.String contents) {
        pre();
        mindustry.gen.Call.clientPacketReliable(playerConnection, type, contents);
        post();
    }
    
    public void clientPacketReliable(java.lang.String type, java.lang.String contents) {
        pre();
        mindustry.gen.Call.clientPacketReliable(type, contents);
        post();
    }
    
    public void blockSnapshot(short amount, short dataLen, byte[] data) {
        pre();
        mindustry.gen.Call.blockSnapshot(amount, dataLen, data);
        post();
    }
    
    public void blockSnapshot(mindustry.net.NetConnection playerConnection, short amount, short dataLen, byte[] data) {
        pre();
        mindustry.gen.Call.blockSnapshot(playerConnection, amount, dataLen, data);
        post();
    }
    
    public void announce(java.lang.String message) {
        pre();
        mindustry.gen.Call.announce(message);
        post();
    }
    
    public void announce(mindustry.net.NetConnection playerConnection, java.lang.String message) {
        pre();
        mindustry.gen.Call.announce(playerConnection, message);
        post();
    }
    
    private static class StaticNet extends LoggableNet {
        
        NetProvider provider;
        
        public StaticNet() {
            super(null);
        }
        
        public StaticNet setProvider(NetProvider provider) {
            this.provider = provider;
            return this;
        }
        
        public StaticNet setNet(Net net) {
            this.net = net;
            return this;
        }
        
        @Override
        public void send(Object object, SendMode mode) {
            if (provider != null) {
                provider.sendClient(object, mode);
                provider = null;
            }
            if (net != null) {
                net.send(object, mode);
                net = null;
            }
        }
    }
}

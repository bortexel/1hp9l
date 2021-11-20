package ru.bortexel.hardcore;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bortexel.hardcore.storage.SQLiteDataStorage;
import ru.bortexel.hardcore.storage.StoredPlayerData;

public class ScoreboardManager {
    private static final Logger logger = LoggerFactory.getLogger("ScoreboardManager");
    private static final String POINTS_OBJECTIVE = "lives";
    
    private final Scoreboard scoreboard;
    private ScoreboardObjective pointsObjective;

    public ScoreboardManager(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void init() {
        if (!this.getScoreboard().containsObjective(POINTS_OBJECTIVE)) this.initPointsObjective();
        this.setPointsObjective(this.getScoreboard().getObjective("lives"));
        this.forceUpdate();
    }

    public void forceUpdate() {
        MinecraftServer server = BortexelHardcore.getInstance().getServer();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            this.forceUpdate(player);
        }
    }

    public void forceUpdate(ServerPlayerEntity player) {
        try {
            BortexelHardcore bortexel = BortexelHardcore.getInstance();
            SQLiteDataStorage storage = bortexel.getStorage();
            StoredPlayerData playerData = storage.getPlayerData(player);
            ScoreboardPlayerScore score = this.getScoreboard().getPlayerScore(player.getEntityName(), this.getPointsObjective());

            MinecraftServer server = bortexel.getServer();
            if (playerData.getPoints() != score.getScore()) {
                score.setScore(playerData.getPoints());
                this.getScoreboard().updateScore(score);
            }

            server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
        } catch (Exception e) {
            logger.error("Unable to update scoreboard", e);
        }
    }
    
    private void initPointsObjective() {
        ScoreboardObjective objective = this.getScoreboard().addObjective(
                POINTS_OBJECTIVE,
                ScoreboardCriterion.DUMMY,
                new LiteralText("Жизни"),
                ScoreboardCriterion.RenderType.INTEGER
        );

        this.getScoreboard().setObjectiveSlot(0, objective);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ScoreboardObjective getPointsObjective() {
        return pointsObjective;
    }

    protected void setPointsObjective(ScoreboardObjective pointsObjective) {
        this.pointsObjective = pointsObjective;
    }
}

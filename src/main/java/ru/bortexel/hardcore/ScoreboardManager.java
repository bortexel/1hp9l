package ru.bortexel.hardcore;

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
    private static final String LIVES_OBJECTIVE = "lives";
    
    private final Scoreboard scoreboard;
    private ScoreboardObjective livesObjective;

    public ScoreboardManager(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void init() {
        if (!this.getScoreboard().containsObjective(LIVES_OBJECTIVE)) this.initLivesObjective();
        this.setLivesObjective(this.getScoreboard().getObjective("lives"));
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
            SQLiteDataStorage storage = BortexelHardcore.getInstance().getStorage();
            StoredPlayerData playerData = storage.getPlayerData(player);
            ScoreboardPlayerScore score = this.getScoreboard().getPlayerScore(player.getEntityName(), this.getLivesObjective());
            score.setScore(playerData.getLives());
            this.getScoreboard().updateScore(score);
        } catch (Exception e) {
            logger.error("Unable to update scoreboard", e);
        }
    }
    
    private void initLivesObjective() {
        ScoreboardObjective objective = this.getScoreboard().addObjective(
                LIVES_OBJECTIVE,
                ScoreboardCriterion.DUMMY,
                new LiteralText("Жизни"),
                ScoreboardCriterion.RenderType.INTEGER
        );

        this.getScoreboard().setObjectiveSlot(0, objective);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ScoreboardObjective getLivesObjective() {
        return livesObjective;
    }

    protected void setLivesObjective(ScoreboardObjective livesObjective) {
        this.livesObjective = livesObjective;
    }
}

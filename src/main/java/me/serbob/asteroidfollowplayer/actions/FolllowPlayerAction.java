package me.serbob.asteroidfollowplayer.actions;

import me.serbob.asteroidapi.actions.abstracts.Action;
import me.serbob.asteroidapi.actions.enums.ActionState;
import me.serbob.asteroidapi.actions.enums.ActionType;
import me.serbob.asteroidapi.actions.enums.Priority;
import me.serbob.asteroidapi.actions.example.PathingAction;
import me.serbob.asteroidapi.enums.Pose;
import me.serbob.asteroidapi.looking.Target;
import me.serbob.asteroidapi.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class FolllowPlayerAction extends Action {
    private final Player target;

    public FolllowPlayerAction(Player target) {
        super(ActionType.FOLLOWING);

        this.target = target;

        /*
         * Asteroid persistently checks for validators
         */
        addValidator(action -> target != null && target.isOnline());
    }

    @Override
    public void onStart(StartType startType) {
        /*
         * Simple monitoring...
         * Really doesn't do anything yet, but it's good to use it (for the future)
         */
        getMonitor().recordStateChange(getState(), ActionState.RUNNING);

        /*
         * The target name can be anything
         * Just be sure to remove it at the end
         */
        getFakePlayer().getLookController()
                .addTarget("action_follow_target", new Target(target, Priority.HIGHEST));
    }

    @Override
    public void onUpdate() {
        Location possibleBlock = LocationUtils.findSolidBlock(
                target.getLocation().clone(),
                BlockFace.DOWN
        ).getLocation();

        PathingAction newPath = new PathingAction(
                possibleBlock,
                PathingAction.MovementType.SPRINT_JUMP
        );
               // .setArrivalDistance() -> You can choose your own arrival distance, I won't use it for our case
               // .setLookAtDestination(false, Priority.NORMAL)

        run(newPath);
    }

    @Override
    public void onStop(StopType stopType) {
        getFakePlayer().getLookController()
                .removeTarget("action_follow_target");

        getFakePlayer().getOverrides().setPose(Pose.STANDING);
        getFakePlayer().getEntityPlayer().setSprinting(false);
    }

    @Override
    public boolean canStart(StartType startType) {
        return true;
    }

    @Override
    public boolean canStop(StopType stopType) {
        return true;
    }
}

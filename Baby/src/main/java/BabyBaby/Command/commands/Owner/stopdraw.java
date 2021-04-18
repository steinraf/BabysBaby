package BabyBaby.Command.commands.Owner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class stopdraw implements OwnerCMD{

    @Override
    public String getName() {
        return "stopdraw";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        draw.on = !draw.on;
        if(draw.on){
            new draw().drawing();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Turn draw on and off.");
    }
    
}

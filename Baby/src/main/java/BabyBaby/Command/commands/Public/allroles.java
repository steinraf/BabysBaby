package BabyBaby.Command.commands.Public;

import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class allroles implements PublicCMD{

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "allroles";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        List<Role> tmp = ctx.getGuild().getRoles();

        String mention = "";
        for (Role var : tmp) {
            mention += var.getAsMention() + " (" + var.getId() + ") \n";
        }
        /*
        if(mention.length() <= 2000){
            String end = mention; 
            event.getChannel().sendMessage("wait a sec").queue(response -> {
                response.editMessage(end).queue();
            });
            
        } else {	
            while(mention.length() > 2000){
                String end = mention.substring(0, 2000); 
                event.getChannel().sendMessage("wait a sec").queue(response -> {
                    response.editMessage(end).queue();
                });
                mention.substring(2000, mention.length());
            }
        }
        */	
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Roles");
        eb.setColor(1);
        int dooku = 0;
        while(mention.length() > 1024){
            String submention = mention.substring(0, 1024);
            String[] part = submention.split("\n");
            submention = mention.substring(0, 1024 - part[part.length-1].length());
            eb.addField(""+ dooku, submention, true);
            mention = mention.substring(1024-part[part.length-1].length());
            dooku++;
        }
        eb.addField(""+ dooku, mention, true);
        
        
        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
    
        ctx.getChannel().sendMessage(eb.build()).queue();
        
        ctx.getMessage().addReaction(":checkmark:769279808244809798").queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all roles from this server and also their IDs.");
    }
    
}

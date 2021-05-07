package BabyBaby.Command.commands.Public;

import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.image.RenderedImage;
import java.io.IOException;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class PlaceGifCMD implements PublicCMD {
    File inp;

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
        List<String> cmds = ctx.getArgs();
        if(Boolean.parseBoolean(cmds.get(0)))
            inp = new File(data.PLACE + cmds.get(1)  + ".txt");       
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "gif";
    }

    @Override
    public void handlePublic(CommandContext ctx) {

        if(inp==null){
            try {
                Attachment placefile = ctx.getMessage().getAttachments().get(0);
                inp = placefile.downloadToFile().join();
            } catch (Exception e) {
                ctx.getChannel().sendMessage("You probably didn't include the file").queue(); 
            }
        }


        try {
            
            ImageOutputStream output = new FileImageOutputStream(new File(data.PLACE + ctx.getAuthor().getId() + ".gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
        
            Scanner scanner = new Scanner(inp);
            int lineCnt = 0; // imgCnt = 0;
            BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < img.getHeight(); i++) {
                for (int j = 0; j < img.getWidth(); j++) {
                    img.setRGB(i, j, new Color(54, 57, 63).getRGB());
                }
            }

            while (scanner.hasNextLine()) {
                String[] s = scanner.nextLine().split(" ");
                img.setRGB(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Color.decode(s[2]).getRGB());
                if (++lineCnt % 2000 == 0) {
                    writer.writeToSequence(img);
                }
            }
            scanner.close();
            writer.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.getChannel().sendFile(new File(data.PLACE + ctx.getAuthor().getId() + ".gif")).queue();

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Make a gif for place.");
    }
    
}

class GifSequenceWriter {

	protected ImageWriter writer;
	protected ImageWriteParam params;
	protected IIOMetadata metadata;

	public GifSequenceWriter(ImageOutputStream out, int imageType, int delay, boolean loop) throws IOException {
		writer = ImageIO.getImageWritersBySuffix("gif").next();
		params = writer.getDefaultWriteParam();

		ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
		metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, params);

		configureRootMetadata(delay, loop);

		writer.setOutput(out);
		writer.prepareWriteSequence(null);
	}

	private void configureRootMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
		String metaFormatName = metadata.getNativeMetadataFormatName();
		IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);

		IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
		graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
		graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
		graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10));
		graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

		IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
		commentsNode.setAttribute("CommentExtension", "Created by: https://memorynotfound.com");

		IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
		IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
		child.setAttribute("applicationID", "NETSCAPE");
		child.setAttribute("authenticationCode", "2.0");

		int loopContinuously = loop ? 0 : 1;
		child.setUserObject(
				new byte[] { 0x1, (byte) (loopContinuously & 0xFF), (byte) ((loopContinuously >> 8) & 0xFF) });
		appExtensionsNode.appendChild(child);
		metadata.setFromTree(metaFormatName, root);
	}

	private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
		int nNodes = rootNode.getLength();
		for (int i = 0; i < nNodes; i++) {
			if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
				return (IIOMetadataNode) rootNode.item(i);
			}
		}
		IIOMetadataNode node = new IIOMetadataNode(nodeName);
		rootNode.appendChild(node);
		return (node);
	}
    public void writeToSequence(RenderedImage img) throws IOException {
		writer.writeToSequence(new IIOImage(img, null, metadata), params);
	}

	public void close() throws IOException {
		writer.endWriteSequence();
	}

}
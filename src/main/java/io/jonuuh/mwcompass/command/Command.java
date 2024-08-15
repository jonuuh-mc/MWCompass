//package io.jonuuh.mwcompass.command;
//
//import io.jonuuh.mwcompass.MWCompass;
//import io.jonuuh.mwcompass.gui.Gui;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.command.CommandBase;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.util.BlockPos;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
//
//import java.util.List;
//
//public class Command extends CommandBase
//{
//
//    private final Gui gui;
//
//    public Command()
//    {
//        this.gui = new Gui();
//    }
//
//    @Override
//    public String getCommandName()
//    {
//        return MWCompass.ModID;
//    }
//
//    @Override
//    public String getCommandUsage(ICommandSender sender)
//    {
//        return "";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel()
//    {
//        return 0;
//    }
//
//    @Override
//    public void processCommand(ICommandSender sender, String[] args)
//    {
//        if (!(sender.getCommandSenderEntity() instanceof EntityPlayerSP))
//        {
//            return;
//        }
//
//        MinecraftForge.EVENT_BUS.register(this);
//    }
//
//    // Need to wait until the start of the next tick to display GUI
//    @SubscribeEvent
//    public void onClientTick(ClientTickEvent event)
//    {
//        Minecraft.getMinecraft().displayGuiScreen(gui);
//        MinecraftForge.EVENT_BUS.unregister(this);
//    }
//
//    @Override
//    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
//    {
//        if (args.length == 1)
//        {
//            return getListOfStringsMatchingLastWord(args, "<commandNames>");
//        }
//        return null;
//    }
//}

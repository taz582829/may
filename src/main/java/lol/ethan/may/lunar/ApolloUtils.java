package lol.ethan.may.lunar;

import com.google.protobuf.Any;
import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import com.lunarclient.apollo.configurable.v1.ConfigurableSettings;
import com.lunarclient.apollo.configurable.v1.OverrideConfigurableSettingsMessage;
import com.lunarclient.apollo.option.SimpleOption;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ApolloUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Method channelRead0;

    static {
        try {
            channelRead0 = NetworkManager.class.getDeclaredMethod("channelRead0", ChannelHandlerContext.class, Packet.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetSettings(SimpleOption<?> option) {
        ConfigurableSettings settings = ConfigurableSettings.newBuilder()
                .setApolloModule(option.getKey())
                .setEnable(false)
                .build();
        OverrideConfigurableSettingsMessage msg = OverrideConfigurableSettingsMessage.newBuilder()
                .addConfigurableSettings(settings)
                .build();

        sendPacket(msg);
    }
    public static void sendPacket(OverrideConfigurableSettingsMessage message) {
        byte[] bytes = Any.pack(message).toByteArray();
        S3FPacketCustomPayload packet = new S3FPacketCustomPayload("lunar:apollo", new PacketBuffer(Unpooled.wrappedBuffer(bytes)));
        mc.addScheduledTask(() -> {
            NetHandlerPlayClient handler = mc.getNetHandler();
            if (handler == null) return;
            NetworkManager manager = handler.getNetworkManager();
            channelRead0.setAccessible(true);
            try {
                channelRead0.invoke(manager, null, packet);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static void setModStatus(String mod, Boolean enabled) {
        Value value = (enabled == null)
                ? Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build()
                : Value.newBuilder().setBoolValue(enabled).build();
        ConfigurableSettings modSetting = ConfigurableSettings.newBuilder()
                .setApolloModule("mod_setting").setEnable(true).build();
        ConfigurableSettings target = ConfigurableSettings.newBuilder()
                .setApolloModule(mod).setEnable(true)
                .putProperties("enabled", value)
                .build();

        OverrideConfigurableSettingsMessage msg = OverrideConfigurableSettingsMessage.newBuilder()
                .addConfigurableSettings(modSetting)
                .addConfigurableSettings(target)
                .build();
        sendPacket(msg);
    }
    public static void setModSetting(SimpleOption<Boolean> option, Value value) {
        ConfigurableSettings settings = ConfigurableSettings.newBuilder()
                .setApolloModule("mod_setting")
                .setEnable(true)
                .putProperties(option.getKey(), value)
                .build();
        System.out.println(settings);
        OverrideConfigurableSettingsMessage message =
                OverrideConfigurableSettingsMessage.newBuilder()
                        .addConfigurableSettings(settings)
                        .build();

        sendPacket(message);
    }
    public static ConfigurableSettings createModuleMessage(String module, Map<String, Value> properties) {
        ConfigurableSettings.Builder moduleBuilder = ConfigurableSettings.newBuilder()
                .setApolloModule(module)
                .setEnable(true);

        if (properties != null) {
            moduleBuilder.putAllProperties(properties);
        }

        return moduleBuilder.build();
    }
//    public static void enableModules() {
//        List<ConfigurableSettings> settings = APOLLO_MODULES.stream()
//                .map(module -> createModuleMessage(module, CONFIG_MODULE_PROPERTIES.row(module)))
//                .collect(Collectors.toList());
//
//        OverrideConfigurableSettingsMessage message = OverrideConfigurableSettingsMessage
//                .newBuilder()
//                .addAllConfigurableSettings(settings)
//                .build();
//
//
//    }

}

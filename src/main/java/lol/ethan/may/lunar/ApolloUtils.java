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
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetSettings(final SimpleOption<?> option) {
        final ConfigurableSettings settings = ConfigurableSettings.newBuilder()
                .setApolloModule(option.getKey())
                .setEnable(false)
                .build();
        final OverrideConfigurableSettingsMessage msg = OverrideConfigurableSettingsMessage.newBuilder()
                .addConfigurableSettings(settings)
                .build();

        sendPacket(msg);
    }
    public static void sendPacket(final OverrideConfigurableSettingsMessage message) {
        final byte[] bytes = Any.pack(message).toByteArray();
        final S3FPacketCustomPayload packet = new S3FPacketCustomPayload("lunar:apollo", new PacketBuffer(Unpooled.wrappedBuffer(bytes)));
        mc.addScheduledTask(() -> {
            final NetHandlerPlayClient handler = mc.getNetHandler();
            if (handler == null) return;
            final NetworkManager manager = handler.getNetworkManager();
            channelRead0.setAccessible(true);
            try {
                channelRead0.invoke(manager, null, packet);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public static void setModStatus(final String mod, final Boolean enabled) {
        final Value value = (enabled == null)
                ? Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build()
                : Value.newBuilder().setBoolValue(enabled).build();
        final ConfigurableSettings modSetting = ConfigurableSettings.newBuilder()
                .setApolloModule("mod_setting").setEnable(true).build();
        final ConfigurableSettings target = ConfigurableSettings.newBuilder()
                .setApolloModule(mod).setEnable(true)
                .putProperties("enabled", value)
                .build();

        final OverrideConfigurableSettingsMessage msg = OverrideConfigurableSettingsMessage.newBuilder()
                .addConfigurableSettings(modSetting)
                .addConfigurableSettings(target)
                .build();
        sendPacket(msg);
    }
    public static void setModSetting(final SimpleOption<Boolean> option, final Value value) {
        final ConfigurableSettings settings = ConfigurableSettings.newBuilder()
                .setApolloModule("mod_setting")
                .setEnable(true)
                .putProperties(option.getKey(), value)
                .build();
        System.out.println(settings);
        final OverrideConfigurableSettingsMessage message =
                OverrideConfigurableSettingsMessage.newBuilder()
                        .addConfigurableSettings(settings)
                        .build();

        sendPacket(message);
    }
    public static ConfigurableSettings createModuleMessage(final String module, final Map<String, Value> properties) {
        final ConfigurableSettings.Builder moduleBuilder = ConfigurableSettings.newBuilder()
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

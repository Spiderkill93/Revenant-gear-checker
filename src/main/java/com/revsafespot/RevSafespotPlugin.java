package com.revsafespot;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@PluginDescriptor(
	name = "Revenant Gear Checker",
	description = "Shows whether your gear is set up correctly to safespot revenants",
	tags = {"rev", "revenant", "safespot", "wilderness", "hcim"}
)
public class RevSafespotPlugin extends Plugin
{
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private RevSafespotPanel panel;

	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Revenant Gear Checker")
			.icon(icon)
			.priority(10)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		panel.refresh();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			panel.refresh();
		}
	}

	@Provides
	RevSafespotConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RevSafespotConfig.class);
	}
}

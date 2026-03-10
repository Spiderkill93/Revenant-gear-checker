package com.revsafespot;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.swing.SwingUtilities;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Revenant Gear Checker",
	description = "Shows whether your gear is set up correctly to safespot revenants",
	tags = {"rev", "revenant", "safespot", "wilderness", "hcim"}
)
public class RevSafespotPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private RevSafespotConfig config;

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
		SwingUtilities.invokeLater(panel::refresh);
		log.debug("Revenant Gear Checker started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		log.debug("Rev Safespot Checker stopped!");
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			SwingUtilities.invokeLater(panel::refresh);
		}
	}

	@Provides
	RevSafespotConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RevSafespotConfig.class);
	}
}

package com.revsafespot;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RevSafespotPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RevSafespotPlugin.class);
		RuneLite.main(args);
	}
}

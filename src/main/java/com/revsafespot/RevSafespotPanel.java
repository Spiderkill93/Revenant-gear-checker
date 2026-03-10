package com.revsafespot;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RevSafespotPanel extends PluginPanel
{
	private static final Color COLOR_SAFE    = new Color(0x44cc44);
	private static final Color COLOR_DANGER  = new Color(0xee4444);
	private static final Color COLOR_LABEL   = new Color(0x9c8b7a);
	private static final Color COLOR_VALUE   = new Color(0xe8ddd0);
	private static final Color COLOR_CARD    = new Color(0x1a1210);

	private final Client client;
	private final ItemManager itemManager;

	// stat value labels
	private final JLabel valMagic    = makeValueLabel("-");
	private final JLabel valDefence  = makeValueLabel("-");
	private final JLabel valStab     = makeValueLabel("-");
	private final JLabel valSlash    = makeValueLabel("-");
	private final JLabel valCrush    = makeValueLabel("-");
	private final JLabel valRanged   = makeValueLabel("-");
	private final JLabel valMagicDef = makeValueLabel("-");

	private final JLabel statusLabel = new JLabel("—");

	@Inject
	RevSafespotPanel(Client client, ItemManager itemManager)
	{
		this.client = client;
		this.itemManager = itemManager;

		setLayout(new BorderLayout(0, 8));
		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		add(buildHeader(),  BorderLayout.NORTH);
		add(buildBody(),    BorderLayout.CENTER);
	}

	// ── Header ────────────────────────────────────────────────────────────────

	private JPanel buildHeader()
	{
		JPanel p = new JPanel(new BorderLayout(0, 4));
		p.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JLabel title = new JLabel("Revenant Gear Checker");
		title.setFont(FontManager.getRunescapeBoldFont());
		title.setForeground(COLOR_VALUE);

		JLabel sub = new JLabel("<html><font color='#9c8b7a'>Revenants pick the attack style you have<br>the lowest defence against. On certain tiles<br>they will attempt to melee you, giving you<br>the opportunity to safespot them.<br>See hcim.net for the full guide.</font></html>");
		sub.setFont(FontManager.getRunescapeSmallFont());

		p.add(title, BorderLayout.NORTH);
		p.add(sub,   BorderLayout.SOUTH);
		return p;
	}

	// ── Body ──────────────────────────────────────────────────────────────────

	private JPanel buildBody()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(ColorScheme.DARK_GRAY_COLOR);

		p.add(Box.createVerticalStrut(6));
		p.add(buildCard("Your levels", new String[][]{
			{"Magic",   null},
			{"Defence", null}
		}, new JLabel[]{valMagic, valDefence}));

		p.add(Box.createVerticalStrut(6));
		p.add(buildCard("Defence bonuses", new String[][]{
			{"Stab def",       null},
			{"Slash def",      null},
			{"Crush def",      null},
			{"Ranged def",     null},
			{"Eff. magic def", null}
		}, new JLabel[]{valStab, valSlash, valCrush, valRanged, valMagicDef}));

		p.add(Box.createVerticalStrut(6));
		p.add(buildStatusCard());

		return p;
	}

	private JPanel buildCard(String heading, String[][] rows, JLabel[] valueLabels)
	{
		JPanel card = new JPanel(new GridBagLayout());
		card.setBackground(COLOR_CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(0x991b1b, true), 1),
			new EmptyBorder(8, 10, 8, 10)
		));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 0, 1, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// heading
		JLabel h = new JLabel(heading.toUpperCase());
		h.setFont(FontManager.getRunescapeSmallFont());
		h.setForeground(new Color(0x991b1b));
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1;
		card.add(h, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 1;
		JSeparator sep = new JSeparator();
		sep.setForeground(new Color(0x991b1b, true));
		gbc.gridx = 0; gbc.gridwidth = 2;
		card.add(sep, gbc);

		for (int i = 0; i < rows.length; i++)
		{
			gbc.gridy = i + 2;
			gbc.gridwidth = 1;

			JLabel lbl = new JLabel(rows[i][0]);
			lbl.setFont(FontManager.getRunescapeSmallFont());
			lbl.setForeground(COLOR_LABEL);
			gbc.gridx = 0; gbc.weightx = 1;
			card.add(lbl, gbc);

			gbc.gridx = 1; gbc.weightx = 0;
			card.add(valueLabels[i], gbc);
		}

		return card;
	}

	private JPanel buildStatusCard()
	{
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(COLOR_CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(0x991b1b, true), 1),
			new EmptyBorder(10, 10, 10, 10)
		));

		statusLabel.setFont(FontManager.getRunescapeBoldFont());
		statusLabel.setForeground(COLOR_LABEL);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

		card.add(statusLabel, BorderLayout.CENTER);
		return card;
	}

	// ── Refresh logic ─────────────────────────────────────────────────────────

	void refresh()
	{
		if (client.getLocalPlayer() == null)
		{
			statusLabel.setText("Not logged in");
			statusLabel.setForeground(COLOR_LABEL);
			return;
		}

		int magic   = client.getBoostedSkillLevel(Skill.MAGIC);
		int defence = client.getBoostedSkillLevel(Skill.DEFENCE);

		int[] b = getDefenceBonuses(); // stab, slash, crush, ranged, magic
		int effectiveMagicDef = (int) Math.floor(magic * 0.7 + defence * 0.3) + b[4];
		int highestMelee = Math.max(b[0], Math.max(b[1], b[2]));

		valMagic.setText(String.valueOf(magic));
		valDefence.setText(String.valueOf(defence));
		valStab.setText(String.valueOf(b[0]));
		valSlash.setText(String.valueOf(b[1]));
		valCrush.setText(String.valueOf(b[2]));
		valRanged.setText(coloredStat(b[3], highestMelee));
		valMagicDef.setText(coloredStat(effectiveMagicDef, highestMelee));

		boolean rangedOk = b[3] > highestMelee;
		boolean magicOk  = effectiveMagicDef > highestMelee;

		if (rangedOk && magicOk)
		{
			statusLabel.setText("<html><center>SAFE<br><font size='3' color='#9c8b7a'>Rev forced into melee</font></center></html>");
			statusLabel.setForeground(COLOR_SAFE);
		}
		else if (!rangedOk && !magicOk)
		{
			statusLabel.setText("<html><center>NOT SAFE<br><font size='3' color='#9c8b7a'>Fix ranged & magic def</font></center></html>");
			statusLabel.setForeground(COLOR_DANGER);
		}
		else if (!rangedOk)
		{
			statusLabel.setText("<html><center>NOT SAFE<br><font size='3' color='#9c8b7a'>Ranged def too low (" + b[3] + " vs " + highestMelee + ")</font></center></html>");
			statusLabel.setForeground(COLOR_DANGER);
		}
		else
		{
			statusLabel.setText("<html><center>NOT SAFE<br><font size='3' color='#9c8b7a'>Magic def too low (" + effectiveMagicDef + " vs " + highestMelee + ")</font></center></html>");
			statusLabel.setForeground(COLOR_DANGER);
		}
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	private static JLabel makeValueLabel(String text)
	{
		JLabel l = new JLabel(text);
		l.setFont(FontManager.getRunescapeSmallFont());
		l.setForeground(COLOR_VALUE);
		l.setHorizontalAlignment(SwingConstants.RIGHT);
		return l;
	}

	/** Returns HTML-coloured value: green if val > threshold, red otherwise */
	private static String coloredStat(int val, int threshold)
	{
		String hex = val > threshold ? "#44cc44" : "#ee4444";
		return "<html><font color='" + hex + "'>" + val + "</font></html>";
	}

	private int[] getDefenceBonuses()
	{
		int stab = 0, slash = 0, crush = 0, ranged = 0, magic = 0;

		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return new int[]{0, 0, 0, 0, 0};
		}

		for (Item item : equipment.getItems())
		{
			if (item == null || item.getId() == -1)
			{
				continue;
			}
			var stats = itemManager.getItemStats(item.getId());
			if (stats == null || stats.getEquipment() == null)
			{
				continue;
			}
			var eq = stats.getEquipment();
			stab   += eq.getDstab();
			slash  += eq.getDslash();
			crush  += eq.getDcrush();
			ranged += eq.getDrange();
			magic  += eq.getDmagic();
		}

		return new int[]{stab, slash, crush, ranged, magic};
	}
}

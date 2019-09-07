/*
 * Copyright (C) 2019 TheJeterLP
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.thejeterlp.commandapi;

import org.bukkit.ChatColor;


public enum CommandResult {


    SUCCESS(null),
    NO_PERMISSION(ChatColor.DARK_RED + "[ERROR] " + ChatColor.GRAY + "You don't have permission for this! " + ChatColor.RED + "(%perm%)"),
    NO_PERMISSION_OTHER(ChatColor.DARK_RED + "[ERROR] " + ChatColor.GRAY + "You don't have permission for this! " + ChatColor.RED + "(%perm%.other)"),
    ERROR(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "Wrong usage! Please type " + ChatColor.GOLD + "/%cmd% help " + ChatColor.GRAY + "!"),
    ONLY_PLAYER(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "This command is only for players!"),
    NOT_ONLINE(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "That player is not online."),
    NOT_A_NUMBER(ChatColor.RED + "[ERROR] " + ChatColor.GRAY + "It has to be a number!");
    
    private final String msg;

    CommandResult(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return this.msg;
    }
}

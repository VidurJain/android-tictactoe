package com.apps.vj.tictactoe.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

public class ComputeComputerMove {

	Integer[] unfilled;

	Set<Integer> filledPlayer1 = new HashSet<Integer>();
	Set<Integer> filledPlayer2 = new HashSet<Integer>();

	HashMap<Integer, Boolean> tictacToe;

	public ComputeComputerMove(HashMap<Integer, Boolean> tictacToe) {
		this.tictacToe = tictacToe;
		int unfilledSize = 9 - tictacToe.size();
		unfilled = new Integer[unfilledSize];
		int i = 0;
		for (int index = 1; index <= 9; index++) {
			if (tictacToe.get(index) == null)
				unfilled[i++] = index;
		}

		for (Entry<Integer, Boolean> entry : tictacToe.entrySet()) {
			if (entry.getValue())
				filledPlayer1.add(entry.getKey());
			else
				filledPlayer2.add(entry.getKey());
		}

	}

	public int computeMove() {
		int attack = findPossibleMove(filledPlayer2);
		int block = findPossibleMove(filledPlayer1);

		return attack == -1 ? (block == -1 ? computeRandom() : block) : attack;
	}

	private int findPossibleMove(Set<Integer> filledPlayer) {
		for (int i = 0; i < 3; i++) {
			if (filledPlayer.contains(3 * i + 1)
					&& filledPlayer.contains(3 * i + 2))
				if (!tictacToe.containsKey(3 * i + 3))
					return 3 * i + 3;
			if (filledPlayer.contains(3 * i + 2)
					&& filledPlayer.contains(3 * i + 3))
				if (!tictacToe.containsKey(3 * i + 1))
					return 3 * i + 1;
			if (filledPlayer.contains(3 * i + 1)
					&& filledPlayer.contains(3 * i + 3))
				if (!tictacToe.containsKey(3 * i + 2))
					return 3 * i + 2;
		}
		for (int i = 0; i < 3; i++) {
			if (filledPlayer.contains(i + 1) && filledPlayer.contains(i + 4))
				if (!tictacToe.containsKey(i + 7))
					return i + 7;
			if (filledPlayer.contains(i + 7) && filledPlayer.contains(i + 4))
				if (!tictacToe.containsKey(i + 1))
					return i + 1;
			if (filledPlayer.contains(i + 7) && filledPlayer.contains(i + 1))
				if (!tictacToe.containsKey(i + 4))
					return i + 4;
		}

		if (filledPlayer.contains(1) && filledPlayer.contains(5))
			if (!tictacToe.containsKey(9))
				return 9;
		if (filledPlayer.contains(1) && filledPlayer.contains(9))
			if (!tictacToe.containsKey(5))
				return 5;
		if (filledPlayer.contains(5) && filledPlayer.contains(9))
			if (!tictacToe.containsKey(1))
				return 1;

		if (filledPlayer.contains(3) && filledPlayer.contains(5))
			if (!tictacToe.containsKey(7))
				return 7;
		if (filledPlayer.contains(3) && filledPlayer.contains(7))
			if (!tictacToe.containsKey(5))
				return 5;
		if (filledPlayer.contains(5) && filledPlayer.contains(7))
			if (!tictacToe.containsKey(3))
				return 3;
		return -1;
	}

	private int computeRandom() {
		Random r = new Random();
		return unfilled[r.nextInt(unfilled.length)];
	}
}

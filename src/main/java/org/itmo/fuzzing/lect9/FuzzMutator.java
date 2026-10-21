package org.itmo.fuzzing.lect9;

/**
 * В репозитории 2025 года базовый мутатор жил в lect9, и lect3.AdvancedMutationFuzzer
 * импортировал его отсюда. В этом репозитории он перенесён в lect3 (тело идентично),
 * поэтому здесь — тонкий наследник, чтобы код lect9 компилировался без изменений.
 */
public class FuzzMutator extends org.itmo.fuzzing.lect3.FuzzMutator {
}

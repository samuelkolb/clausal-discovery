package logic.theory;

/**
 * An abstract class that allows different implementations to fill in different ways to print logical programs
 *
 * @author Samuel Kolb
 */
public abstract class ProgramPrinter {

	/**
	 * Prints a logical program
	 * @param program	The program to be printed
	 * @return	The resulting string
	 */
	public abstract String print(LogicProgram program);

	public abstract String printTheory(Theory theory, String name, String vocabularyName);

	public abstract String printVocabulary(Vocabulary vocabulary, String name);

	public abstract String printStructure(Structure structure, String name, String vocabularyName);

	public String printStructures(LogicProgram program, String prefix, String vocabularyName) {
		if(program.getStructures().isEmpty())
			return "";
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < program.getStructures().size(); i++)
			builder.append(printStructure(program.getStructures().get(i), prefix + i, vocabularyName));
		return builder.toString();
	}
}

// :name=DumpUntranslated :description=Dumps untranslated segment to directory in project path in json format

import groovy.json.*
import org.omegat.core.Core;
import org.omegat.core.data.PrepareTMXEntry;

files = project.getProjectFiles();

def prop = project.projectProperties
if (!prop)
	return

def root = prop.projectRoot;

def wtf = "test"

def dump = root + "/" + "segments_dump"

final FileTreeBuilder treeBuilder = new FileTreeBuilder(new File(root))

fileLoop:
for (i in 0 ..< files.size())
{
	fi = files[i];

	def segments = []
	normalizedPath = fi.filePath.replace("\\", "_").replace("/", "_")
	//console.println(normalizedPath);

	console.println "Loading segments from '" + fi.filePath
	for (j in 0 ..< fi.entries.size())
	{
		ste = fi.entries[j];
		source = ste.getSrcText();
		info = project.getTranslationInfo(ste)
		target = info ? info.translation : null;      	
		//console.println(ste.entryNum() + "\t" + source + "\t" + target + "\t" + (info ? info.isTranslated() : false));
		
		if (target && source != target)
			continue;
      
		if (java.lang.Thread.interrupted()) {
			break fileLoop;
		}
	
		def segment = [
			source: source,
	      	target: source,
			file: fi.filePath
		]

		segments << segment
	}
	
	console.println "Total " + segments.size() + " untranslated segments"

	if (segments.size()) {
		treeBuilder.dir("segments_dump") {
			file(normalizedPath + '.json') {
				withWriter('UTF-8') { writer ->
					console.println "Writing " + normalizedPath + '.json ...'
					writer.write new JsonBuilder(segments).toPrettyString()
				}
			}
		}
	}
}


return

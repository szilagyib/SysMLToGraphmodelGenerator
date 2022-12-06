/*
 * generated by Xtext 2.18.0.M3
 */
package graphmodelgenerator.generator

import org.eclipse.emf.ecore.resource.Resource
import org.omg.sysml.lang.sysml.PartDefinition
import org.omg.sysml.lang.sysml.PartUsage
import org.omg.sysml.lang.sysml.Package
import org.omg.sysml.lang.sysml.Namespace
import org.omg.sysml.lang.sysml.MultiplicityRange
import org.omg.sysml.lang.sysml.LiteralInteger
import org.omg.sysml.lang.sysml.AttributeUsage
import org.omg.sysml.lang.sysml.AttributeDefinition
import org.omg.sysml.lang.sysml.EnumerationDefinition
import org.omg.sysml.lang.sysml.EnumerationUsage
import org.omg.sysml.lang.sysml.LiteralInfinity
import org.omg.sysml.lang.sysml.ActionDefinition
import org.omg.sysml.lang.sysml.SuccessionAsUsage
import org.omg.sysml.lang.sysml.ActionUsage
import org.omg.sysml.lang.sysml.TransitionUsage
import org.omg.sysml.lang.sysml.MergeNode
import org.omg.sysml.lang.sysml.DecisionNode
import java.util.List
import java.util.ArrayList
import java.util.Map
import java.util.HashMap
import org.omg.sysml.lang.sysml.Usage
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext

class SysMLGeneratorProblem extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		val model = resource.contents.get(0) as Namespace
		val name = resource.URI.trimFileExtension().lastSegment()
		fsa.generateFile(name + ".problem", IFileSystemAccess2.DEFAULT_OUTPUT, generate(model))
	}
	
	def String generate(Namespace model) '''
		«FOR p : model.member.filter(Package)»
			«FOR pd : p.member.filter(PartDefinition)»
				«partDefinition(pd)»
				
			«ENDFOR»	
			«FOR ad : p.member.filter(AttributeDefinition)»
				«attributeDefinition(ad)»
				
			«ENDFOR»	
		«ENDFOR»
		«IF !model.member.filter(ActionDefinition).isEmpty»
			«abstractAction()»

		«ENDIF»
		«FOR ad : model.member.filter(ActionDefinition)»
			«actionDefinition(ad)»
		«ENDFOR»
		«IF !model.member.filter(ActionDefinition).isEmpty»
			
			scope domain <= «numOfNodes(model.member.filter(ActionDefinition))».
		«ENDIF»	
	'''

	//StructureDiagram

	def String partDefinition(PartDefinition pd) '''
		«IF pd.isAbstract»
			abstract class «pd.name»«
		ELSE»
			class «pd.name»«
		ENDIF»«
		val super_types = pd.ownedSubclassification»«
		IF super_types.size != 0» extends «
			super_types.get(0).superclassifier.name»«
			FOR i : 1 ..< super_types.size», «
				super_types.get(i).superclassifier.name»«
			ENDFOR»«
		ENDIF» {
		«FOR pu : pd.ownedPart.filter(PartUsage)»
			«"	"»«partUsage(pu)»
		«ENDFOR»
		«FOR attr : pd.ownedAttribute.filter(AttributeUsage)»
			«"	"»«attributeUsage(attr)»
		«ENDFOR»
		}
	'''
	
	def String partUsage(PartUsage pu) '''
		«IF pu.isComposite»contains «ENDIF»«pu.definition.get(0).name» «
		IF pu.multiplicity instanceof MultiplicityRange»«
			multiplicity(pu.multiplicity as MultiplicityRange)»«
		ENDIF»«pu.name»
	'''
	
	def String attributeUsage(AttributeUsage attr) '''
		«attributeType(attr.definition.get(0).name)»«
		IF attr.multiplicity instanceof MultiplicityRange»«
			multiplicity(attr.multiplicity as MultiplicityRange)»«
		ENDIF»«attr.name»
	'''
	
	def String attributeDefinition(AttributeDefinition ad) '''
		«IF ad instanceof EnumerationDefinition»
			enum «ad.name» {
			«val enum_values = ad.member.filter(EnumerationUsage)»«
			IF enum_values.size != 0»«
				"	"»«enum_values.get(0).name»«
				FOR i : 1 ..< enum_values.size», «
					enum_values.get(i).name»«
				ENDFOR»«
			ENDIF»
		«ELSE»
			class «ad.name» {
		«ENDIF»
		«FOR au : ad.ownedAttribute.filter(AttributeUsage)»«
			"	"»«attributeType(au.definition.get(0).name)»«
			IF au.multiplicity instanceof MultiplicityRange»«
				multiplicity(au.multiplicity as MultiplicityRange)»«
			ENDIF»«au.name»
		«ENDFOR»
		}
	'''
	
	def String multiplicity(MultiplicityRange mr) '''«
		val l_bound = mr.lowerBound»«
		val u_bound = mr.upperBound»«
		val bound = mr.bound.get(0)»«
		IF (l_bound !== null) && (u_bound !== null)»[«
			IF l_bound instanceof LiteralInteger»«
				(l_bound as LiteralInteger).value»..«
			ENDIF»«
			IF u_bound instanceof LiteralInfinity»*«
			ELSEIF u_bound instanceof LiteralInteger»«
				(u_bound as LiteralInteger).value»«
			ENDIF»] «
		ELSEIF bound !== null»«
			IF (bound instanceof LiteralInfinity)»[] «
			ELSEIF (bound instanceof LiteralInteger) 
				&& (bound as LiteralInteger).value != 1»[«
				(bound as LiteralInteger).value»] «
			ENDIF»«
		ENDIF»'''
	
	def String attributeType(String type) '''«
		IF (type.equals("Integer")) || (type.equals("UnlimitedNatural"))»int «
		ELSEIF type.equals("String")»string «
		ELSEIF type.equals("Real")»real «
		ELSEIF type.equals("Boolean")»bool «
		ELSE»«type» «
		ENDIF»'''
	
	//ActionDiagram
	
	def String abstractAction() '''
		abstract class Action {
			contains Action[0..1] next
		}
	'''
	
	def String actionDefinition(ActionDefinition ad) '''
		«IF !ad.ownedAction.isEmpty»
			«FOR au : ad.ownedAction»
				«IF !(au instanceof TransitionUsage)»
					class «actionDefName(au)» extends Action {}
				«ENDIF»
			«ENDFOR»
			«IF !(ad.member.filter(SuccessionAsUsage).isEmpty && ad.member.filter(TransitionUsage).isEmpty)»
				
				«actionConstraints(ad)»
			«ENDIF»
		«ENDIF»	
	'''
	
	def String actionConstraints(ActionDefinition ad) '''
		error invalidSuccession(Action first, Action second) :-
			«invalidSuccession(ad.member.filter(SuccessionAsUsage), ad.member.filter(TransitionUsage).isEmpty)»
			«invalidSuccession(ad.member.filter(TransitionUsage))»
			
		«IF hasEndAction(ad)»
			error invalidNumOfNext(Action a) :-
				«invalidNumOfNext(ad, ad.member.filter(SuccessionAsUsage), oneTargetActionMap(ad, ad.member.filter(TransitionUsage)).isEmpty
					&& justEndTargetMap(ad.member.filter(TransitionUsage)).isEmpty)»
				«invalidNumOfNext(ad, ad.member.filter(TransitionUsage))»
			
		«ENDIF»		
		error invalidActionSeq(Action a1, Action a2) :-
		«invalidActionSeq(startActionDefName(ad))»
		
		error multiplePrev(Action a) :-
			next(a, a1),
			next(a, a2),
			!equals(a1, a2).
	'''
	
	def String invalidSuccession(Iterable<SuccessionAsUsage> successions, boolean onlyBlock) '''
		«FOR succ : successions»
			«IF justActionTargets(succ)»
				«actionDefName(succ.source.get(0) as ActionUsage)»(first),
				!«actionDefName(succ.target.get(0) as ActionUsage)»(second),
				next(first, second)«lineEnd(successions, succ, onlyBlock, true)»
			«ENDIF»
		«ENDFOR»
	'''
	
	def String invalidSuccession(Iterable<TransitionUsage> transitions) '''
		«FOR entry : transitionMap(transitions, false).entrySet»
			«actionDefName(entry.getKey)»(first),
			«FOR targetName : entry.getValue»
				!«targetName»(second),
			«ENDFOR»
			next(first, second)«lineEnd(transitionMap(transitions, false), entry.getKey, true)»
		«ENDFOR»
	'''
	
	def String invalidNumOfNext(ActionDefinition ad, Iterable<SuccessionAsUsage> successions, boolean onlyBlock) '''
		«FOR succ : successions»
			«IF justActionTargets(succ) && leadsToEndAction(ad, succ, new ArrayList<Usage>())»
				«actionDefName(succ.source.get(0) as ActionUsage)»(a),
				1 =!= count { next(a, _a) }«lineEnd(successions, succ, onlyBlock, false)»
			«ELSEIF hasTargetEndAction(succ)»
				«actionDefName(succ.source.get(0) as ActionUsage)»(a),
				0 =!= count { next(a, _a) }«lineEnd(successions, succ, onlyBlock, false)»
			«ENDIF»
		«ENDFOR»
	'''
	
	def String invalidNumOfNext(ActionDefinition ad, Iterable<TransitionUsage> transitions) '''
		«val actionTargetMap = oneTargetActionMap(ad, transitions)»«
		val endTargetMap = justEndTargetMap(transitions)»«
		FOR entry : actionTargetMap.entrySet»
			«actionDefName(entry.getKey)»(a),
			1 =!= count { next(a, _a) }«lineEnd(actionTargetMap, entry.getKey, endTargetMap.isEmpty)»
		«ENDFOR»
		«FOR entry : endTargetMap.entrySet»
			«actionDefName(entry.getKey)»(a),
			0 =!= count { next(a, _a) }«lineEnd(endTargetMap, entry.getKey, true)»
		«ENDFOR»
	'''
	
	def String actionDefName(ActionUsage au) '''«
		IF (au instanceof MergeNode)»Merge_«au.name»« 
		ELSEIF (au instanceof DecisionNode)»Decision_«au.name»«
		ELSE»«au.definition.get(0).name»«
		ENDIF»'''
	
	def String startActionDefName(ActionDefinition ad) '''«
		FOR succ : ad.member.filter(SuccessionAsUsage)»«
			IF succ.source.get(0) instanceof ActionUsage && succ.target.get(0) instanceof ActionUsage
					&& succ.source.get(0).name != null && succ.source.get(0).name.equals("start")
					&& succ.target.get(0).name != null && !succ.target.get(0).name.equals("done")»«
				actionDefName(succ.target.get(0) as ActionUsage)»«
			ENDIF»«
		ENDFOR»'''
	
	def String lineEnd(Iterable<SuccessionAsUsage> successions, SuccessionAsUsage succ, boolean onlyBlock, boolean ownActionsOnly) '''«
		IF onlyBlock && succ.equals(lastSuccession(successions, ownActionsOnly))».«
		ELSE»;«
		ENDIF»'''
	
	def String lineEnd(Iterable<TransitionUsage> transitions, TransitionUsage trans, boolean ownActionsOnly) '''«
		IF trans.equals(lastTransition(transitions, ownActionsOnly))».«
		ELSE»;«
		ENDIF»'''
	
	def String lineEnd(Map<ActionUsage, List<String>> transitionMap, ActionUsage key, boolean lastBlock) '''«
		IF lastBlock && key.equals(lastKeyInTransitionMap(transitionMap))».«
		ELSE»;«
		ENDIF»'''
	
	def SuccessionAsUsage lastSuccession(Iterable<SuccessionAsUsage> successions, boolean ownActionsOnly) {
		if (!ownActionsOnly)
			return successions.last;
		val filtered = successions.filter[it.target.get(0).name != null && !it.target.get(0).name.equals("done")];
		return filtered.last;	
	}
	
	def TransitionUsage lastTransition(Iterable<TransitionUsage> transitions, boolean ownActionsOnly) {
		if (!ownActionsOnly)
			return transitions.last;
		val filtered = transitions.filter[it.target.name != null && !it.target.name.equals("done")];
		return filtered.last;	
	}
	
	def String invalidActionSeq(String entryAction) '''
		«"	"»!equals(a1, a2),
		«"	"»isEntry(a1),
		«"	"»isEntry(a2);
		«"	"»!isEntry(a_);
		«"	"»isEntry(a),
		«"	"»!«entryAction»(a).
		
		pred isEntry(Action a) :-
			0 =:= count { notInTransitiveClosure(a, _a) }.
		
		pred notInTransitiveClosure(Action a1, Action a2) :-
			a1 != a2,
			!isInTransitiveClosure(a1, a2).
		
		pred isInTransitiveClosure(Action a1, Action a2) :-
			next+(a1, a2).
	'''
	
	def Map<ActionUsage, List<String>> transitionMap(Iterable<TransitionUsage> transitions, boolean isEnd) {
		val Map<ActionUsage, List<String>> map = new HashMap();
		for (trans : transitions) {
			if (trans.source instanceof ActionUsage && trans.target instanceof ActionUsage
					&& trans.source.name != null && !trans.source.name.equals("start") 
					&& trans.target.name != null && (!isEnd && !trans.target.name.equals("done")
					|| isEnd && trans.target.name.equals("done"))) {
				if (!map.containsKey(trans.source))
					map.put(trans.source, new ArrayList<String>());
				map.get(trans.source).add(actionDefName(trans.target))	
			}		
		}
		return map;
	}
	
	def Map<ActionUsage, List<String>> justEndTargetMap(Iterable<TransitionUsage> transitions) {
		val actionTargetMap = transitionMap(transitions, false);
		val endTargetMap = transitionMap(transitions, true);
		return endTargetMap.filter[k, v | !actionTargetMap.containsKey(k)];
	}
	
	def Map<ActionUsage, List<String>> oneTargetActionMap(ActionDefinition ad, Iterable<TransitionUsage> transitions) {
		val actionTargetMap = transitionMap(transitions, false);
		return actionTargetMap.filter[k, v | v.size > 1 && leadsToEndAction(ad, outEdges(ad, k).get(0), new ArrayList<Usage>())];
	}
	
	def ActionUsage lastKeyInTransitionMap(Map<ActionUsage, List<String>> map) {
		var ActionUsage key;
		for (entry : map.entrySet)
			key = entry.getKey();
		return key;
	}	
	
	def boolean justActionTargets(SuccessionAsUsage succ) {
		if (succ.source.get(0) instanceof ActionUsage && succ.target.get(0) instanceof ActionUsage
					&& succ.source.get(0).name != null && !succ.source.get(0).name.equals("start")
					&& succ.target.get(0).name != null && !succ.target.get(0).name.equals("done"))
				return true;
		return false;
	}	
	
	def boolean hasTargetEndAction(SuccessionAsUsage succ) {
		if (succ.source.get(0) instanceof ActionUsage && succ.target.get(0) instanceof ActionUsage
					&& succ.source.get(0).name != null && !succ.source.get(0).name.equals("start")
					&& succ.target.get(0).name != null && succ.target.get(0).name.equals("done"))
				return true;
		return false;
	}
	
	def boolean hasTargetEndAction(TransitionUsage trans) {
		if (trans.source instanceof ActionUsage && trans.target instanceof ActionUsage
					&& trans.source.name != null && !trans.source.name.equals("start") 
					&& trans.target.name != null && trans.target.name.equals("done"))
				return true;
		return false;
	}
	
	def boolean hasEndAction(ActionDefinition ad) {
		for (succ : ad.member.filter(SuccessionAsUsage))
			if (hasTargetEndAction(succ))
				return true;
		for (trans : ad.member.filter(TransitionUsage))
			if (hasTargetEndAction(trans))
				return true;
		return false;
	}
	
	def List<Usage> outEdges(ActionDefinition ad, Usage u) {
		val List<Usage> sources = new ArrayList();
		for (succ : ad.member.filter(SuccessionAsUsage))
			if (succ.source.get(0).equals(u))
				sources.add(succ);
		for (trans : ad.member.filter(TransitionUsage))
			if (trans.source.equals(u))
				sources.add(trans);
		return sources;
	}
	
	def boolean leadsToEndAction(ActionDefinition ad, Usage u, List<Usage> seen) {
		if (u instanceof SuccessionAsUsage) {
			if (seen.contains(u)) {
				return false;
			}	
			else if (hasTargetEndAction(u as SuccessionAsUsage)) {
				return true;
			}			
			else {
				seen.add(u);
				for (e : outEdges(ad, u.target.get(0) as Usage))
					if (leadsToEndAction(ad, e, seen))
				 		return true;
				return false;
			}
		}
		else if (u instanceof TransitionUsage) {
			if (seen.contains(u)) {
				return false;
			}	
			else if (hasTargetEndAction(u as TransitionUsage)) {
				return true;
			}			
			else {
				seen.add(u);
				for (e : outEdges(ad, u.target as Usage))
					if (leadsToEndAction(ad, e, seen))
				 		return true;
				return false;
			}
		}
		return false;
	}
	
	def int numOfNodes(Iterable<ActionDefinition> actionDefs) {
		var int cnt = 0;
		for (ad : actionDefs)
			for (au : ad.ownedAction)
				if (!(au instanceof TransitionUsage))
					cnt += 1;
		return cnt;
	}
}
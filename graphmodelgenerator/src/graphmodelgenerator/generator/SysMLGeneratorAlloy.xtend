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
import java.util.Collections
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext

class SysMLGeneratorAlloy extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		val model = resource.contents.get(0) as Namespace
		val name = resource.URI.trimFileExtension().lastSegment()
		fsa.generateFile(name + ".als", IFileSystemAccess2.DEFAULT_OUTPUT, generate(model))
	}
	
	def String generate(Namespace model) '''
		«FOR p : model.member.filter(Package)»
			«FOR i : p.importedMembership»
				sig «i.memberElement.name» {}
						
			«ENDFOR»
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
			
			run {} for «numOfNodes(model.member.filter(ActionDefinition))» Action
		«ENDIF»
	'''

	//StructureDiagram

	def String partDefinition(PartDefinition pd) '''
		«IF pd.isAbstract»
			abstract sig «pd.name»«
		ELSE»
			sig «pd.name»«
		ENDIF»«
		val super_types = pd.ownedSubclassification»«
		IF super_types.size != 0» extends «
			super_types.get(0).superclassifier.name»«
			FOR i : 1 ..< super_types.size», «
				super_types.get(i).superclassifier.name»«
			ENDFOR»«
		ENDIF» {
		«FOR pu : pd.ownedPart.filter(PartUsage)»
			«"	"»«partUsage(pu)»«lineEnd(pd.ownedPart.filter(PartUsage), pu)»
		«ENDFOR»
		«FOR attr : pd.ownedAttribute.filter(AttributeUsage)»
			«"	"»«attributeUsage(attr)»«lineEnd(pd.ownedAttribute.filter(AttributeUsage), attr)»
		«ENDFOR»
		}«
		IF !multiplicityConstraints(pd.ownedPart.filter(PartUsage), pd.ownedAttribute.filter(AttributeUsage)).isEmpty» {
			«FOR c: multiplicityConstraints(pd.ownedPart.filter(PartUsage), pd.ownedAttribute.filter(AttributeUsage))»
				«c»
			«ENDFOR»
		}
		«ENDIF»
	'''
	
	def String partUsage(PartUsage pu) '''
		«pu.name»: «
		IF pu.multiplicity instanceof MultiplicityRange»«
			multiplicity(pu.multiplicity as MultiplicityRange)»«
		ENDIF»«pu.definition.get(0).name»'''
	
	def String attributeUsage(AttributeUsage attr) '''
		«attr.name»: «
		IF attr.multiplicity instanceof MultiplicityRange»«
			multiplicity(attr.multiplicity as MultiplicityRange)»«
		ENDIF»«attributeType(attr.definition.get(0).name)»'''
	
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
			sig «ad.name» {
		«ENDIF»
		«FOR au : ad.ownedAttribute.filter(AttributeUsage)»«
			"	"»«au.name»: «
			IF au.multiplicity instanceof MultiplicityRange»«
				multiplicity(au.multiplicity as MultiplicityRange)»«
			ENDIF»«attributeType(au.definition.get(0).name)»
		«ENDFOR»
		}«
		IF !multiplicityConstraints(Collections.emptyList, ad.ownedAttribute.filter(AttributeUsage)).isEmpty» {
			«FOR c: multiplicityConstraints(Collections.emptyList, ad.ownedAttribute.filter(AttributeUsage))»
				«c»
			«ENDFOR»
		}
		«ENDIF»
	'''
	
	def String multiplicity(MultiplicityRange mr) '''«
		val l_bound = mr.lowerBound»«
		val u_bound = mr.upperBound»«
		val bound = mr.bound.get(0)»«
		IF (l_bound !== null) && (u_bound !== null)»«
			IF l_bound instanceof LiteralInteger && u_bound instanceof LiteralInteger
				&& (l_bound as LiteralInteger).value == 0 && (l_bound as LiteralInteger).value == 1»lone «
			ENDIF»«
			IF l_bound instanceof LiteralInteger && (l_bound as LiteralInteger).value == 0»set «
			ELSE»some «
			ENDIF»«
		ELSEIF bound !== null»«
			IF (bound instanceof LiteralInfinity)»set «
			ELSEIF (bound instanceof LiteralInteger) 
				&& (bound as LiteralInteger).value != 1»some «
			ENDIF»«
		ENDIF»'''
	
	def String attributeType(String type) '''«
		IF type.equals("Integer")»Integer «
		ELSEIF type.equals("UnlimitedNatural")»UnlimitedNatural «
		ELSEIF type.equals("String")»String «
		ELSEIF type.equals("Real")»Real «
		ELSEIF type.equals("Boolean")»Boolean «
		ELSE»«type» «
		ENDIF»'''
	
	def String lineEnd(Iterable<PartUsage> parts, PartUsage pu) '''«
		IF !pu.equals(parts.last)»,«
		ENDIF»'''
	
	def String lineEnd(Iterable<AttributeUsage> attributes, AttributeUsage attr) '''«
		IF !attr.equals(attributes.last)»,«
		ENDIF»'''
	
	def List<String> multiplicityConstraints(Iterable<PartUsage> partUsages, Iterable<AttributeUsage> attrUsages) {
		val List<Usage> usages = new ArrayList<Usage>();
		usages.addAll(partUsages);
		usages.addAll(attrUsages);
		val constraints = new ArrayList<String>();
		for (u : usages) {
			if (u.multiplicity instanceof MultiplicityRange) {
				val l_bound = (u.multiplicity as MultiplicityRange).lowerBound
				val u_bound = (u.multiplicity as MultiplicityRange).upperBound
				val bound = (u.multiplicity as MultiplicityRange).bound.get(0)
				if (l_bound != null && u_bound != null) {
					if (l_bound instanceof LiteralInteger && (l_bound as LiteralInteger).value != 0 && (l_bound as LiteralInteger).value != 1)
						constraints.add("#" + u.name + " >= " + (l_bound as LiteralInteger).value);
					if (u_bound instanceof LiteralInteger)
						constraints.add("#" + u.name + " <= " + (u_bound as LiteralInteger).value);
				}
				else if (bound != null && bound instanceof LiteralInteger && (bound as LiteralInteger).value != 1) {
					constraints.add("#" + u.name + " = " + (bound as LiteralInteger).value);
				}
			}		
		}
		return constraints;
	}
	
	//ActionDiagram
	
	def String abstractAction() '''
		abstract sig Action {
			next: lone Action
		}
	'''
	
	def String actionDefinition(ActionDefinition ad) '''
		«IF !ad.ownedAction.isEmpty»
			«FOR au : ad.ownedAction»
				«IF !(au instanceof TransitionUsage)»
					sig «actionDefName(au)» extends Action {}
				«ENDIF»
			«ENDFOR»
			«IF !(ad.member.filter(SuccessionAsUsage).isEmpty && ad.member.filter(TransitionUsage).isEmpty)»
				
				«actionConstraints(ad)»
			«ENDIF»
		«ENDIF»	
	'''
	
	def String actionConstraints(ActionDefinition ad) '''
		fact correctSuccession {
			«correctSuccession(ad.member.filter(SuccessionAsUsage), ad.member.filter(TransitionUsage))»
		}
			
		«IF hasEndAction(ad)»
			fact numOfNext {
				«numOfNext(ad, ad.member.filter(SuccessionAsUsage), ad.member.filter(TransitionUsage))»
			}

		«ENDIF»		
		fact actionSeq {
			«actionSeq(startActionDefName(ad))»
		}
		
		fact lonePrev {
			all a: Action | (lone _a: Action | a in _a.next)
		}
	'''
	
	def String correctSuccession(Iterable<SuccessionAsUsage> successions, Iterable<TransitionUsage> transitions) '''
		«FOR succ : successions»
			«IF justActionTargets(succ)»
				all a: «actionDefName(succ.source.get(0) as ActionUsage)» | a.next in «actionDefName(succ.target.get(0) as ActionUsage)»
			«ENDIF»
		«ENDFOR»
		«FOR entry : transitionMap(transitions, false).entrySet»
			all a: «actionDefName(entry.getKey)» | a.next in «
				FOR targetName : entry.getValue»«
					targetName»«IF !targetName.equals(entry.getValue.last)» + «ENDIF»«
				ENDFOR»
		«ENDFOR»
	'''
	
	def String numOfNext(ActionDefinition ad, Iterable<SuccessionAsUsage> successions, Iterable<TransitionUsage> transitions) '''
		«FOR succ : successions»
			«IF justActionTargets(succ) && leadsToEndAction(ad, succ, new ArrayList<Usage>())»
				all a: «actionDefName(succ.source.get(0) as ActionUsage)» | (one _a: Action | _a in a.next)
			«ELSEIF hasTargetEndAction(succ)»
				all a: «actionDefName(succ.source.get(0) as ActionUsage)» | (no _a: Action | _a in a.next)
			«ENDIF»
		«ENDFOR»
		«val actionTargetMap = oneTargetActionMap(ad, transitions)»«
		val endTargetMap = justEndTargetMap(transitions)»«
		FOR entry : actionTargetMap.entrySet»
			all a: «actionDefName(entry.getKey)» | (one _a: Action | _a in a.next)
		«ENDFOR»
		«FOR entry : endTargetMap.entrySet»
			all a: «actionDefName(entry.getKey)» | (no _a: Action | _a in a.next)
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
	
	def actionSeq(String entryAction) '''
		one a: Action | (all _a: Action | _a != a <=> _a in a.^next) and a in «entryAction»
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
/**
 * generated by Xtext 2.18.0.M3
 */
package graphmodelgenerator.generator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.ExclusiveRange;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.omg.sysml.lang.sysml.ActionDefinition;
import org.omg.sysml.lang.sysml.ActionUsage;
import org.omg.sysml.lang.sysml.AttributeDefinition;
import org.omg.sysml.lang.sysml.AttributeUsage;
import org.omg.sysml.lang.sysml.DecisionNode;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.EnumerationDefinition;
import org.omg.sysml.lang.sysml.EnumerationUsage;
import org.omg.sysml.lang.sysml.Expression;
import org.omg.sysml.lang.sysml.LiteralInfinity;
import org.omg.sysml.lang.sysml.LiteralInteger;
import org.omg.sysml.lang.sysml.MergeNode;
import org.omg.sysml.lang.sysml.Multiplicity;
import org.omg.sysml.lang.sysml.MultiplicityRange;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.PartDefinition;
import org.omg.sysml.lang.sysml.PartUsage;
import org.omg.sysml.lang.sysml.Subclassification;
import org.omg.sysml.lang.sysml.SuccessionAsUsage;
import org.omg.sysml.lang.sysml.TransitionUsage;
import org.omg.sysml.lang.sysml.Usage;

@SuppressWarnings("all")
public class SysMLGeneratorViatra extends AbstractGenerator {
  @Override
  public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
    EObject _get = resource.getContents().get(0);
    final Namespace model = ((Namespace) _get);
    final String name = resource.getURI().trimFileExtension().lastSegment();
    fsa.generateFile((name + ".xcore"), IFileSystemAccess2.DEFAULT_OUTPUT, this.generateXcore(model));
    fsa.generateFile((name + ".vql"), IFileSystemAccess2.DEFAULT_OUTPUT, this.generateVql(model));
    fsa.generateFile((name + ".vsconfig"), IFileSystemAccess2.DEFAULT_OUTPUT, this.generateVsconfig(model));
  }
  
  public String generateXcore(final Namespace model) {
    StringConcatenation _builder = new StringConcatenation();
    {
      Iterable<org.omg.sysml.lang.sysml.Package> _filter = Iterables.<org.omg.sysml.lang.sysml.Package>filter(model.getMember(), org.omg.sysml.lang.sysml.Package.class);
      for(final org.omg.sysml.lang.sysml.Package p : _filter) {
        {
          Iterable<PartDefinition> _filter_1 = Iterables.<PartDefinition>filter(p.getMember(), PartDefinition.class);
          for(final PartDefinition pd : _filter_1) {
            String _partDefinition = this.partDefinition(pd);
            _builder.append(_partDefinition);
            _builder.newLineIfNotEmpty();
            _builder.newLine();
          }
        }
        {
          Iterable<AttributeDefinition> _filter_2 = Iterables.<AttributeDefinition>filter(p.getMember(), AttributeDefinition.class);
          for(final AttributeDefinition ad : _filter_2) {
            String _attributeDefinition = this.attributeDefinition(ad);
            _builder.append(_attributeDefinition);
            _builder.newLineIfNotEmpty();
            _builder.newLine();
          }
        }
      }
    }
    {
      boolean _isEmpty = IterableExtensions.isEmpty(Iterables.<ActionDefinition>filter(model.getMember(), ActionDefinition.class));
      boolean _not = (!_isEmpty);
      if (_not) {
        String _abstractAction = this.abstractAction();
        _builder.append(_abstractAction);
        _builder.newLineIfNotEmpty();
        _builder.newLine();
      }
    }
    {
      Iterable<ActionDefinition> _filter_3 = Iterables.<ActionDefinition>filter(model.getMember(), ActionDefinition.class);
      for(final ActionDefinition ad_1 : _filter_3) {
        String _actionDefinition = this.actionDefinition(ad_1);
        _builder.append(_actionDefinition);
        _builder.newLineIfNotEmpty();
        _builder.newLine();
      }
    }
    return _builder.toString();
  }
  
  public String generateVql(final Namespace model) {
    StringConcatenation _builder = new StringConcatenation();
    {
      Iterable<ActionDefinition> _filter = Iterables.<ActionDefinition>filter(model.getMember(), ActionDefinition.class);
      for(final ActionDefinition ad : _filter) {
        {
          boolean _not = (!(IterableExtensions.isEmpty(Iterables.<SuccessionAsUsage>filter(ad.getMember(), SuccessionAsUsage.class)) && IterableExtensions.isEmpty(Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class))));
          if (_not) {
            String _actionConstraints = this.actionConstraints(ad);
            _builder.append(_actionConstraints);
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String generateVsconfig(final Namespace model) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("import epackage \"<<metamodel_path>>\"\"");
    _builder.newLine();
    _builder.append("import viatra \"<<vql_path>>\"");
    _builder.newLine();
    _builder.newLine();
    _builder.append("generate {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("metamodel = {package action}");
    _builder.newLine();
    _builder.append(" \t");
    _builder.append("constraints = {package queries}");
    _builder.newLine();
    _builder.append(" \t");
    _builder.append("solver = ViatraSolver");
    _builder.newLine();
    _builder.append(" \t");
    _builder.append("scope = {#node = 0..");
    int _numOfNodes = this.numOfNodes(Iterables.<ActionDefinition>filter(model.getMember(), ActionDefinition.class));
    _builder.append(_numOfNodes, " \t");
    _builder.append("}");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("config = {log-level = normal, \"scopePropagator\" = \"typeHierarchy\"}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("number = 1");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("output = \"<<out_path>>\"");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String partDefinition(final PartDefinition pd) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isAbstract = pd.isAbstract();
      if (_isAbstract) {
        _builder.append("abstract class ");
        String _name = pd.getName();
        _builder.append(_name);
      } else {
        _builder.newLineIfNotEmpty();
        _builder.append("class ");
        String _name_1 = pd.getName();
        _builder.append(_name_1);
      }
    }
    final EList<Subclassification> super_types = pd.getOwnedSubclassification();
    {
      int _size = super_types.size();
      boolean _notEquals = (_size != 0);
      if (_notEquals) {
        _builder.append(" extends ");
        String _name_2 = super_types.get(0).getSuperclassifier().getName();
        _builder.append(_name_2);
        {
          int _size_1 = super_types.size();
          ExclusiveRange _doubleDotLessThan = new ExclusiveRange(1, _size_1, true);
          for(final Integer i : _doubleDotLessThan) {
            _builder.append(", ");
            String _name_3 = super_types.get((i).intValue()).getSuperclassifier().getName();
            _builder.append(_name_3);
          }
        }
      }
    }
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    {
      Iterable<PartUsage> _filter = Iterables.<PartUsage>filter(pd.getOwnedPart(), PartUsage.class);
      for(final PartUsage pu : _filter) {
        _builder.append("\t");
        String _partUsage = this.partUsage(pu);
        _builder.append(_partUsage);
        _builder.newLineIfNotEmpty();
      }
    }
    {
      Iterable<AttributeUsage> _filter_1 = Iterables.<AttributeUsage>filter(pd.getOwnedAttribute(), AttributeUsage.class);
      for(final AttributeUsage attr : _filter_1) {
        _builder.append("\t");
        String _attributeUsage = this.attributeUsage(attr);
        _builder.append(_attributeUsage);
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String partUsage(final PartUsage pu) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isComposite = pu.isComposite();
      if (_isComposite) {
        _builder.append("contains ");
      } else {
        _builder.append("refers ");
      }
    }
    String _name = pu.getDefinition().get(0).getName();
    _builder.append(_name);
    _builder.append(" ");
    {
      Multiplicity _multiplicity = pu.getMultiplicity();
      if ((_multiplicity instanceof MultiplicityRange)) {
        Multiplicity _multiplicity_1 = pu.getMultiplicity();
        String _multiplicity_2 = this.multiplicity(((MultiplicityRange) _multiplicity_1));
        _builder.append(_multiplicity_2);
      }
    }
    String _name_1 = pu.getName();
    _builder.append(_name_1);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String attributeUsage(final AttributeUsage attr) {
    StringConcatenation _builder = new StringConcatenation();
    String _attributeType = this.attributeType(attr.getDefinition().get(0).getName());
    _builder.append(_attributeType);
    {
      Multiplicity _multiplicity = attr.getMultiplicity();
      if ((_multiplicity instanceof MultiplicityRange)) {
        Multiplicity _multiplicity_1 = attr.getMultiplicity();
        String _multiplicity_2 = this.multiplicity(((MultiplicityRange) _multiplicity_1));
        _builder.append(_multiplicity_2);
      }
    }
    String _name = attr.getName();
    _builder.append(_name);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String attributeDefinition(final AttributeDefinition ad) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if ((ad instanceof EnumerationDefinition)) {
        _builder.append("enum ");
        String _name = ((EnumerationDefinition)ad).getName();
        _builder.append(_name);
        _builder.append(" {");
        _builder.newLineIfNotEmpty();
        final Iterable<EnumerationUsage> enum_values = Iterables.<EnumerationUsage>filter(((EnumerationDefinition)ad).getMember(), EnumerationUsage.class);
        {
          for(final EnumerationUsage e : enum_values) {
            _builder.append("\t");
            String _name_1 = e.getName();
            _builder.append(_name_1);
          }
        }
        _builder.newLineIfNotEmpty();
      } else {
        _builder.append("class ");
        String _name_2 = ad.getName();
        _builder.append(_name_2);
        _builder.append(" {");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      Iterable<AttributeUsage> _filter = Iterables.<AttributeUsage>filter(ad.getOwnedAttribute(), AttributeUsage.class);
      for(final AttributeUsage au : _filter) {
        _builder.append("\t");
        String _attributeType = this.attributeType(au.getDefinition().get(0).getName());
        _builder.append(_attributeType);
        {
          Multiplicity _multiplicity = au.getMultiplicity();
          if ((_multiplicity instanceof MultiplicityRange)) {
            Multiplicity _multiplicity_1 = au.getMultiplicity();
            String _multiplicity_2 = this.multiplicity(((MultiplicityRange) _multiplicity_1));
            _builder.append(_multiplicity_2);
          }
        }
        String _name_3 = au.getName();
        _builder.append(_name_3);
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String multiplicity(final MultiplicityRange mr) {
    StringConcatenation _builder = new StringConcatenation();
    final Expression l_bound = mr.getLowerBound();
    final Expression u_bound = mr.getUpperBound();
    final Expression bound = mr.getBound().get(0);
    {
      if (((l_bound != null) && (u_bound != null))) {
        _builder.append("[");
        {
          if ((l_bound instanceof LiteralInteger)) {
            int _value = ((LiteralInteger) l_bound).getValue();
            _builder.append(_value);
            _builder.append("..");
          }
        }
        {
          if ((u_bound instanceof LiteralInfinity)) {
            _builder.append("*");
          } else {
            if ((u_bound instanceof LiteralInteger)) {
              int _value_1 = ((LiteralInteger) u_bound).getValue();
              _builder.append(_value_1);
            }
          }
        }
        _builder.append("] ");
      } else {
        if ((bound != null)) {
          {
            if ((bound instanceof LiteralInfinity)) {
              _builder.append("[] ");
            } else {
              if (((bound instanceof LiteralInteger) && (((LiteralInteger) bound).getValue() != 1))) {
                _builder.append("[");
                int _value_2 = ((LiteralInteger) bound).getValue();
                _builder.append(_value_2);
                _builder.append("] ");
              }
            }
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String attributeType(final String type) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if ((type.equals("Integer") || type.equals("UnlimitedNatural"))) {
        _builder.append("Integer ");
      } else {
        boolean _equals = type.equals("String");
        if (_equals) {
          _builder.append("String ");
        } else {
          boolean _equals_1 = type.equals("Real");
          if (_equals_1) {
            _builder.append("Double ");
          } else {
            boolean _equals_2 = type.equals("Boolean");
            if (_equals_2) {
              _builder.append("Boolean ");
            } else {
              _builder.append(type);
              _builder.append(" ");
            }
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String abstractAction() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package action");
    _builder.newLine();
    _builder.newLine();
    _builder.append("abstract class Action {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("contains Action[0..1] next");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String actionDefinition(final ActionDefinition ad) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = ad.getOwnedAction().isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        {
          EList<ActionUsage> _ownedAction = ad.getOwnedAction();
          for(final ActionUsage au : _ownedAction) {
            {
              if ((!(au instanceof TransitionUsage))) {
                _builder.append("class ");
                String _actionDefName = this.actionDefName(au);
                _builder.append(_actionDefName);
                _builder.append(" extends Action {}");
                _builder.newLineIfNotEmpty();
              }
            }
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String actionConstraints(final ActionDefinition ad) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package queries");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import epackage \"<<metamodel_nsuri>>\"");
    _builder.newLine();
    _builder.newLine();
    _builder.append("@Constraint");
    _builder.newLine();
    _builder.append("pattern invalidSuccession(first: Action, second: Action) {");
    _builder.newLine();
    String _invalidSuccession = this.invalidSuccession(Iterables.<SuccessionAsUsage>filter(ad.getMember(), SuccessionAsUsage.class), IterableExtensions.isEmpty(Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class)));
    _builder.append(_invalidSuccession);
    _builder.newLineIfNotEmpty();
    String _invalidSuccession_1 = this.invalidSuccession(Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class));
    _builder.append(_invalidSuccession_1);
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    {
      boolean _hasEndAction = this.hasEndAction(ad);
      if (_hasEndAction) {
        _builder.append("@Constraint");
        _builder.newLine();
        _builder.append("pattern invalidNumOfNext(a: Action) {");
        _builder.newLine();
        Iterable<SuccessionAsUsage> _filter = Iterables.<SuccessionAsUsage>filter(ad.getMember(), SuccessionAsUsage.class);
        String _invalidNumOfNext = this.invalidNumOfNext(ad, _filter, (this.oneTargetActionMap(ad, Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class)).isEmpty() && this.justEndTargetMap(Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class)).isEmpty()));
        _builder.append(_invalidNumOfNext);
        _builder.newLineIfNotEmpty();
        String _invalidNumOfNext_1 = this.invalidNumOfNext(ad, Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class));
        _builder.append(_invalidNumOfNext_1);
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("pattern hasNext(a: Action) {");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("Action.next(a, _a);");
        _builder.newLine();
        _builder.append("}");
        _builder.newLine();
        _builder.newLine();
      }
    }
    _builder.append("@Constraint");
    _builder.newLine();
    _builder.append("pattern invalidActionSeq(a1: Action, a2: Action) {");
    _builder.newLine();
    String _invalidActionSeq = this.invalidActionSeq(this.startActionDefName(ad));
    _builder.append(_invalidActionSeq);
    _builder.append("\t\t");
    _builder.newLineIfNotEmpty();
    String _isActionType = this.isActionType(ad);
    _builder.append(_isActionType);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String invalidSuccession(final Iterable<SuccessionAsUsage> successions, final boolean onlyBlock) {
    StringConcatenation _builder = new StringConcatenation();
    {
      for(final SuccessionAsUsage succ : successions) {
        {
          boolean _justActionTargets = this.justActionTargets(succ);
          if (_justActionTargets) {
            _builder.append("\t");
            Element _get = succ.getSource().get(0);
            String _actionDefName = this.actionDefName(((ActionUsage) _get));
            _builder.append(_actionDefName);
            _builder.append("(first);");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("neg find is");
            Element _get_1 = succ.getTarget().get(0);
            String _actionDefName_1 = this.actionDefName(((ActionUsage) _get_1));
            _builder.append(_actionDefName_1);
            _builder.append("(second);");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("Action.next(first, second);");
            _builder.newLineIfNotEmpty();
            String _lineEnd = this.lineEnd(successions, succ, onlyBlock, true);
            _builder.append(_lineEnd);
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String invalidSuccession(final Iterable<TransitionUsage> transitions) {
    StringConcatenation _builder = new StringConcatenation();
    {
      Set<Map.Entry<ActionUsage, List<String>>> _entrySet = this.transitionMap(transitions, false).entrySet();
      for(final Map.Entry<ActionUsage, List<String>> entry : _entrySet) {
        _builder.append("\t");
        String _actionDefName = this.actionDefName(entry.getKey());
        _builder.append(_actionDefName);
        _builder.append("(first);");
        _builder.newLineIfNotEmpty();
        {
          List<String> _value = entry.getValue();
          for(final String targetName : _value) {
            _builder.append("\t");
            _builder.append("neg find is");
            _builder.append(targetName);
            _builder.append("(second);");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("\t");
        _builder.append("Action.next(first, second);");
        _builder.newLineIfNotEmpty();
        String _lineEnd = this.lineEnd(this.transitionMap(transitions, false), entry.getKey(), true);
        _builder.append(_lineEnd);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder.toString();
  }
  
  public String invalidNumOfNext(final ActionDefinition ad, final Iterable<SuccessionAsUsage> successions, final boolean onlyBlock) {
    StringConcatenation _builder = new StringConcatenation();
    {
      for(final SuccessionAsUsage succ : successions) {
        {
          if ((this.justActionTargets(succ) && this.leadsToEndAction(ad, succ, new ArrayList<Usage>()))) {
            _builder.append("\t");
            Element _get = succ.getSource().get(0);
            String _actionDefName = this.actionDefName(((ActionUsage) _get));
            _builder.append(_actionDefName);
            _builder.append("(a);");
            _builder.newLineIfNotEmpty();
            _builder.append("\t");
            _builder.append("neg find hasNext(a);");
            _builder.newLineIfNotEmpty();
            String _lineEnd = this.lineEnd(successions, succ, onlyBlock, false);
            _builder.append(_lineEnd);
            _builder.newLineIfNotEmpty();
          } else {
            boolean _hasTargetEndAction = this.hasTargetEndAction(succ);
            if (_hasTargetEndAction) {
              _builder.append("\t");
              Element _get_1 = succ.getSource().get(0);
              String _actionDefName_1 = this.actionDefName(((ActionUsage) _get_1));
              _builder.append(_actionDefName_1);
              _builder.append("(a);");
              _builder.newLineIfNotEmpty();
              _builder.append("\t");
              _builder.append("find hasNext(a);");
              _builder.newLineIfNotEmpty();
              String _lineEnd_1 = this.lineEnd(successions, succ, onlyBlock, false);
              _builder.append(_lineEnd_1);
              _builder.newLineIfNotEmpty();
            }
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String invalidNumOfNext(final ActionDefinition ad, final Iterable<TransitionUsage> transitions) {
    StringConcatenation _builder = new StringConcatenation();
    final Map<ActionUsage, List<String>> actionTargetMap = this.oneTargetActionMap(ad, transitions);
    final Map<ActionUsage, List<String>> endTargetMap = this.justEndTargetMap(transitions);
    {
      Set<Map.Entry<ActionUsage, List<String>>> _entrySet = actionTargetMap.entrySet();
      for(final Map.Entry<ActionUsage, List<String>> entry : _entrySet) {
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        String _actionDefName = this.actionDefName(entry.getKey());
        _builder.append(_actionDefName);
        _builder.append("(a);");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("neg find hasNext(a);");
        _builder.newLineIfNotEmpty();
        String _lineEnd = this.lineEnd(actionTargetMap, entry.getKey(), endTargetMap.isEmpty());
        _builder.append(_lineEnd);
        _builder.newLineIfNotEmpty();
      }
    }
    {
      Set<Map.Entry<ActionUsage, List<String>>> _entrySet_1 = endTargetMap.entrySet();
      for(final Map.Entry<ActionUsage, List<String>> entry_1 : _entrySet_1) {
        _builder.append("\t");
        String _actionDefName_1 = this.actionDefName(entry_1.getKey());
        _builder.append(_actionDefName_1);
        _builder.append("(a);");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("find hasNext(a);");
        _builder.newLineIfNotEmpty();
        String _lineEnd_1 = this.lineEnd(endTargetMap, entry_1.getKey(), true);
        _builder.append(_lineEnd_1);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder.toString();
  }
  
  public String actionDefName(final ActionUsage au) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if ((au instanceof MergeNode)) {
        _builder.append("Merge_");
        String _name = ((MergeNode)au).getName();
        _builder.append(_name);
      } else {
        if ((au instanceof DecisionNode)) {
          _builder.append("Decision_");
          String _name_1 = ((DecisionNode)au).getName();
          _builder.append(_name_1);
        } else {
          String _name_2 = au.getDefinition().get(0).getName();
          _builder.append(_name_2);
        }
      }
    }
    return _builder.toString();
  }
  
  public String startActionDefName(final ActionDefinition ad) {
    StringConcatenation _builder = new StringConcatenation();
    {
      Iterable<SuccessionAsUsage> _filter = Iterables.<SuccessionAsUsage>filter(ad.getMember(), SuccessionAsUsage.class);
      for(final SuccessionAsUsage succ : _filter) {
        {
          if (((((((succ.getSource().get(0) instanceof ActionUsage) && (succ.getTarget().get(0) instanceof ActionUsage)) && (!Objects.equal(succ.getSource().get(0).getName(), null))) && succ.getSource().get(0).getName().equals("start")) && (!Objects.equal(succ.getTarget().get(0).getName(), null))) && (!succ.getTarget().get(0).getName().equals("done")))) {
            Element _get = succ.getTarget().get(0);
            String _actionDefName = this.actionDefName(((ActionUsage) _get));
            _builder.append(_actionDefName);
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public String lineEnd(final Iterable<SuccessionAsUsage> successions, final SuccessionAsUsage succ, final boolean onlyBlock, final boolean ownActionsOnly) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if ((onlyBlock && succ.equals(this.lastSuccession(successions, ownActionsOnly)))) {
        _builder.append("}");
        _builder.newLine();
      } else {
        _builder.append("} or {");
        _builder.newLine();
      }
    }
    return _builder.toString();
  }
  
  public String lineEnd(final Iterable<TransitionUsage> transitions, final TransitionUsage trans, final boolean ownActionsOnly) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _equals = trans.equals(this.lastTransition(transitions, ownActionsOnly));
      if (_equals) {
        _builder.append("}");
        _builder.newLine();
      } else {
        _builder.append("} or {");
        _builder.newLine();
      }
    }
    return _builder.toString();
  }
  
  public String lineEnd(final Map<ActionUsage, List<String>> transitionMap, final ActionUsage key, final boolean lastBlock) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if ((lastBlock && key.equals(this.lastKeyInTransitionMap(transitionMap)))) {
        _builder.append("}");
        _builder.newLine();
      } else {
        _builder.append("} or {");
        _builder.newLine();
      }
    }
    return _builder.toString();
  }
  
  public SuccessionAsUsage lastSuccession(final Iterable<SuccessionAsUsage> successions, final boolean ownActionsOnly) {
    if ((!ownActionsOnly)) {
      return IterableExtensions.<SuccessionAsUsage>last(successions);
    }
    final Function1<SuccessionAsUsage, Boolean> _function = (SuccessionAsUsage it) -> {
      return Boolean.valueOf(((!Objects.equal(it.getTarget().get(0).getName(), null)) && (!it.getTarget().get(0).getName().equals("done"))));
    };
    final Iterable<SuccessionAsUsage> filtered = IterableExtensions.<SuccessionAsUsage>filter(successions, _function);
    return IterableExtensions.<SuccessionAsUsage>last(filtered);
  }
  
  public TransitionUsage lastTransition(final Iterable<TransitionUsage> transitions, final boolean ownActionsOnly) {
    if ((!ownActionsOnly)) {
      return IterableExtensions.<TransitionUsage>last(transitions);
    }
    final Function1<TransitionUsage, Boolean> _function = (TransitionUsage it) -> {
      return Boolean.valueOf(((!Objects.equal(it.getTarget().getName(), null)) && (!it.getTarget().getName().equals("done"))));
    };
    final Iterable<TransitionUsage> filtered = IterableExtensions.<TransitionUsage>filter(transitions, _function);
    return IterableExtensions.<TransitionUsage>last(filtered);
  }
  
  public String invalidActionSeq(final String entryAction) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t");
    _builder.append("a1 != a2;");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("find isEntry(a1);");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("find isEntry(a2);");
    _builder.newLineIfNotEmpty();
    _builder.append("} or {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("neg find isEntry(_a);");
    _builder.newLineIfNotEmpty();
    _builder.append("} or {\t\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("neg find is");
    _builder.append(entryAction);
    _builder.append("(a);");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("find isEntry(a);");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("pattern isEntry(a: Action) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("neg find notInTransitiveClosure(a, _a);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("pattern notInTransitiveClosure(a1: Action, a2: Action) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("a1 != a2;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("neg find isInTransitiveClosure(a1, a2);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("pattern isInTransitiveClosure(a1: Action, a2: Action) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("find isNext+(a1, a2);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("pattern isNext(a1: Action, a2: Action) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("Action.next(a1, a2);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String isActionType(final ActionDefinition ad) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = ad.getOwnedAction().isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        {
          EList<ActionUsage> _ownedAction = ad.getOwnedAction();
          for(final ActionUsage au : _ownedAction) {
            {
              if ((!(au instanceof TransitionUsage))) {
                _builder.newLine();
                _builder.append("pattern is");
                String _actionDefName = this.actionDefName(au);
                _builder.append(_actionDefName);
                _builder.append("(a: Action) {");
                _builder.newLineIfNotEmpty();
                _builder.append("\t");
                String _actionDefName_1 = this.actionDefName(au);
                _builder.append(_actionDefName_1, "\t");
                _builder.append("(a);");
                _builder.newLineIfNotEmpty();
                _builder.append("}");
                _builder.newLine();
              }
            }
          }
        }
      }
    }
    return _builder.toString();
  }
  
  public Map<ActionUsage, List<String>> transitionMap(final Iterable<TransitionUsage> transitions, final boolean isEnd) {
    final Map<ActionUsage, List<String>> map = new HashMap<ActionUsage, List<String>>();
    for (final TransitionUsage trans : transitions) {
      if (((((((trans.getSource() instanceof ActionUsage) && (trans.getTarget() instanceof ActionUsage)) && (!Objects.equal(trans.getSource().getName(), null))) && (!trans.getSource().getName().equals("start"))) && (!Objects.equal(trans.getTarget().getName(), null))) && (((!isEnd) && (!trans.getTarget().getName().equals("done"))) || (isEnd && trans.getTarget().getName().equals("done"))))) {
        boolean _containsKey = map.containsKey(trans.getSource());
        boolean _not = (!_containsKey);
        if (_not) {
          ActionUsage _source = trans.getSource();
          ArrayList<String> _arrayList = new ArrayList<String>();
          map.put(_source, _arrayList);
        }
        map.get(trans.getSource()).add(this.actionDefName(trans.getTarget()));
      }
    }
    return map;
  }
  
  public Map<ActionUsage, List<String>> justEndTargetMap(final Iterable<TransitionUsage> transitions) {
    final Map<ActionUsage, List<String>> actionTargetMap = this.transitionMap(transitions, false);
    final Map<ActionUsage, List<String>> endTargetMap = this.transitionMap(transitions, true);
    final Function2<ActionUsage, List<String>, Boolean> _function = (ActionUsage k, List<String> v) -> {
      boolean _containsKey = actionTargetMap.containsKey(k);
      return Boolean.valueOf((!_containsKey));
    };
    return MapExtensions.<ActionUsage, List<String>>filter(endTargetMap, _function);
  }
  
  public Map<ActionUsage, List<String>> oneTargetActionMap(final ActionDefinition ad, final Iterable<TransitionUsage> transitions) {
    final Map<ActionUsage, List<String>> actionTargetMap = this.transitionMap(transitions, false);
    final Function2<ActionUsage, List<String>, Boolean> _function = (ActionUsage k, List<String> v) -> {
      return Boolean.valueOf(((v.size() > 1) && this.leadsToEndAction(ad, this.outEdges(ad, k).get(0), new ArrayList<Usage>())));
    };
    return MapExtensions.<ActionUsage, List<String>>filter(actionTargetMap, _function);
  }
  
  public ActionUsage lastKeyInTransitionMap(final Map<ActionUsage, List<String>> map) {
    ActionUsage key = null;
    Set<Map.Entry<ActionUsage, List<String>>> _entrySet = map.entrySet();
    for (final Map.Entry<ActionUsage, List<String>> entry : _entrySet) {
      key = entry.getKey();
    }
    return key;
  }
  
  public boolean justActionTargets(final SuccessionAsUsage succ) {
    if (((((((succ.getSource().get(0) instanceof ActionUsage) && (succ.getTarget().get(0) instanceof ActionUsage)) && (!Objects.equal(succ.getSource().get(0).getName(), null))) && (!succ.getSource().get(0).getName().equals("start"))) && (!Objects.equal(succ.getTarget().get(0).getName(), null))) && (!succ.getTarget().get(0).getName().equals("done")))) {
      return true;
    }
    return false;
  }
  
  public boolean hasTargetEndAction(final SuccessionAsUsage succ) {
    if (((((((succ.getSource().get(0) instanceof ActionUsage) && (succ.getTarget().get(0) instanceof ActionUsage)) && (!Objects.equal(succ.getSource().get(0).getName(), null))) && (!succ.getSource().get(0).getName().equals("start"))) && (!Objects.equal(succ.getTarget().get(0).getName(), null))) && succ.getTarget().get(0).getName().equals("done"))) {
      return true;
    }
    return false;
  }
  
  public boolean hasTargetEndAction(final TransitionUsage trans) {
    if (((((((trans.getSource() instanceof ActionUsage) && (trans.getTarget() instanceof ActionUsage)) && (!Objects.equal(trans.getSource().getName(), null))) && (!trans.getSource().getName().equals("start"))) && (!Objects.equal(trans.getTarget().getName(), null))) && trans.getTarget().getName().equals("done"))) {
      return true;
    }
    return false;
  }
  
  public boolean hasEndAction(final ActionDefinition ad) {
    Iterable<SuccessionAsUsage> _filter = Iterables.<SuccessionAsUsage>filter(ad.getMember(), SuccessionAsUsage.class);
    for (final SuccessionAsUsage succ : _filter) {
      boolean _hasTargetEndAction = this.hasTargetEndAction(succ);
      if (_hasTargetEndAction) {
        return true;
      }
    }
    Iterable<TransitionUsage> _filter_1 = Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class);
    for (final TransitionUsage trans : _filter_1) {
      boolean _hasTargetEndAction_1 = this.hasTargetEndAction(trans);
      if (_hasTargetEndAction_1) {
        return true;
      }
    }
    return false;
  }
  
  public List<Usage> outEdges(final ActionDefinition ad, final Usage u) {
    final List<Usage> sources = new ArrayList<Usage>();
    Iterable<SuccessionAsUsage> _filter = Iterables.<SuccessionAsUsage>filter(ad.getMember(), SuccessionAsUsage.class);
    for (final SuccessionAsUsage succ : _filter) {
      boolean _equals = succ.getSource().get(0).equals(u);
      if (_equals) {
        sources.add(succ);
      }
    }
    Iterable<TransitionUsage> _filter_1 = Iterables.<TransitionUsage>filter(ad.getMember(), TransitionUsage.class);
    for (final TransitionUsage trans : _filter_1) {
      boolean _equals_1 = trans.getSource().equals(u);
      if (_equals_1) {
        sources.add(trans);
      }
    }
    return sources;
  }
  
  public boolean leadsToEndAction(final ActionDefinition ad, final Usage u, final List<Usage> seen) {
    if ((u instanceof SuccessionAsUsage)) {
      boolean _contains = seen.contains(u);
      if (_contains) {
        return false;
      } else {
        boolean _hasTargetEndAction = this.hasTargetEndAction(((SuccessionAsUsage) u));
        if (_hasTargetEndAction) {
          return true;
        } else {
          seen.add(u);
          Element _get = ((SuccessionAsUsage)u).getTarget().get(0);
          List<Usage> _outEdges = this.outEdges(ad, ((Usage) _get));
          for (final Usage e : _outEdges) {
            boolean _leadsToEndAction = this.leadsToEndAction(ad, e, seen);
            if (_leadsToEndAction) {
              return true;
            }
          }
          return false;
        }
      }
    } else {
      if ((u instanceof TransitionUsage)) {
        boolean _contains_1 = seen.contains(u);
        if (_contains_1) {
          return false;
        } else {
          boolean _hasTargetEndAction_1 = this.hasTargetEndAction(((TransitionUsage) u));
          if (_hasTargetEndAction_1) {
            return true;
          } else {
            seen.add(u);
            ActionUsage _target = ((TransitionUsage)u).getTarget();
            List<Usage> _outEdges_1 = this.outEdges(ad, ((Usage) _target));
            for (final Usage e_1 : _outEdges_1) {
              boolean _leadsToEndAction_1 = this.leadsToEndAction(ad, e_1, seen);
              if (_leadsToEndAction_1) {
                return true;
              }
            }
            return false;
          }
        }
      }
    }
    return false;
  }
  
  public int numOfNodes(final Iterable<ActionDefinition> actionDefs) {
    int cnt = 0;
    for (final ActionDefinition ad : actionDefs) {
      EList<ActionUsage> _ownedAction = ad.getOwnedAction();
      for (final ActionUsage au : _ownedAction) {
        if ((!(au instanceof TransitionUsage))) {
          int _cnt = cnt;
          cnt = (_cnt + 1);
        }
      }
    }
    return cnt;
  }
}

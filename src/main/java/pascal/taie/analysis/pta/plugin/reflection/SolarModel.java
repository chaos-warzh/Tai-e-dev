package pascal.taie.analysis.pta.plugin.reflection;

import pascal.taie.analysis.pta.core.cs.context.Context;
import pascal.taie.analysis.pta.core.cs.element.CSObj;
import pascal.taie.analysis.pta.core.cs.element.CSVar;
import pascal.taie.analysis.pta.core.heap.Descriptor;
import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.analysis.pta.core.solver.Solver;
import pascal.taie.analysis.pta.pts.PointsToSet;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.Invoke;
import pascal.taie.language.classes.ClassNames;
import pascal.taie.language.classes.JMethod;
import pascal.taie.language.type.ClassType;

import java.util.Set;

import static pascal.taie.analysis.pta.plugin.util.InvokeUtils.BASE;

class SolarModel extends StringBasedModel {

    /**
     * Descriptor for the unknown objects generated by reflective invocation.
     */
    private static final Descriptor UNKNOWN_DESC = () -> "UnknownReflectiveObj";

    private final ClassType object;

    SolarModel(Solver solver, MetaObjHelper helper, Set<Invoke> invokesWithLog) {
        super(solver, helper, invokesWithLog);
        object = typeSystem.getClassType(ClassNames.OBJECT);
    }

    @Override
    protected void registerVarAndHandler() {
        super.registerVarAndHandler();

        JMethod classNewInstance = hierarchy.getJREMethod("<java.lang.Class: java.lang.Object newInstance()>");
        registerRelevantVarIndexes(classNewInstance, BASE);
        registerAPIHandler(classNewInstance, this::classNewInstance);
    }

    @Override
    protected void classForName(CSVar csVar, PointsToSet pts, Invoke invoke) {
        if (invokesWithLog.contains(invoke)) {
            return;
        }
        // invoke super's method to handle string constants
        super.classForName(csVar, pts, invoke);
        // process unknown strings
        Context context = csVar.getContext();
        pts.forEach(obj -> {
            if (!heapModel.isStringConstant(obj.getObject())) {
                Var result = invoke.getResult();
                if (result != null) {
                    Obj unknownClass = helper.getUnknownMetaObj(invoke, helper.clazz);
                    solver.addVarPointsTo(context, result, unknownClass);
                }
            }
        });
    }

    private void classNewInstance(CSVar csVar, PointsToSet pts, Invoke invoke) {
        if (invokesWithLog.contains(invoke)) {
            return;
        }
        Var result = invoke.getResult();
        if (result != null) {
            Context context = csVar.getContext();
            for (CSObj obj : pts) {
                if (helper.isUnknownMetaObj(obj)) {
                    Obj unknownObj = heapModel.getMockObj(UNKNOWN_DESC, invoke, object,
                            invoke.getContainer(), false);
                    solver.addVarPointsTo(context, result, unknownObj);
                    return;
                }
            }
        }
    }
}
/* This file was generated by SableCC (http://www.sablecc.org/). */

package de.tuberlin.cs.cis.ocl.parser.analysis;

import de.tuberlin.cs.cis.ocl.parser.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseAOclFile(AOclFile node);
    void caseAConstraint(AConstraint node);
    void caseADefinitionContextBodypart(ADefinitionContextBodypart node);
    void caseAConstraintContextBodypart(AConstraintContextBodypart node);
    void caseAContextDeclaration(AContextDeclaration node);
    void caseAOperationContextKind(AOperationContextKind node);
    void caseAClassifierContextKind(AClassifierContextKind node);
    void caseAReturnType(AReturnType node);
    void caseAClassifierType(AClassifierType node);
    void caseAPreConditionStereotype(APreConditionStereotype node);
    void caseAPostConditionStereotype(APostConditionStereotype node);
    void caseAInvariantStereotype(AInvariantStereotype node);
    void caseAContextOperationName(AContextOperationName node);
    void caseALogicalContextOperationName(ALogicalContextOperationName node);
    void caseARelationalContextOperationName(ARelationalContextOperationName node);
    void caseAAddContextOperationName(AAddContextOperationName node);
    void caseAMultiplyContextOperationName(AMultiplyContextOperationName node);
    void caseAFormalParameterList(AFormalParameterList node);
    void caseAParamList(AParamList node);
    void caseAFormalParameter(AFormalParameter node);
    void caseANextParam(ANextParam node);
    void caseAOclExpression(AOclExpression node);
    void caseALetDeclaration(ALetDeclaration node);
    void caseALetExpression(ALetExpression node);
    void caseALetParameterList(ALetParameterList node);
    void caseATypePostfix(ATypePostfix node);
    void caseAIfExpression(AIfExpression node);
    void caseAExpression(AExpression node);
    void caseALogicalExpression(ALogicalExpression node);
    void caseAImplication(AImplication node);
    void caseABooleanExpression(ABooleanExpression node);
    void caseABooleanOperation(ABooleanOperation node);
    void caseARelationalExpression(ARelationalExpression node);
    void caseAEquation(AEquation node);
    void caseACompareableExpression(ACompareableExpression node);
    void caseAComparison(AComparison node);
    void caseAAdditiveExpression(AAdditiveExpression node);
    void caseAAddition(AAddition node);
    void caseAMultiplicativeExpression(AMultiplicativeExpression node);
    void caseAMultiplication(AMultiplication node);
    void caseAPrefixedUnaryExpression(APrefixedUnaryExpression node);
    void caseAUnaryExpression(AUnaryExpression node);
    void caseAPostfixExpression(APostfixExpression node);
    void caseAObjectPropertyInvocation(AObjectPropertyInvocation node);
    void caseACollectionPropertyInvocation(ACollectionPropertyInvocation node);
    void caseACollectionPrimaryExpression(ACollectionPrimaryExpression node);
    void caseALiteralPrimaryExpression(ALiteralPrimaryExpression node);
    void caseAPropertyCallPrimaryExpression(APropertyCallPrimaryExpression node);
    void caseAParenthesedPrimaryExpression(AParenthesedPrimaryExpression node);
    void caseAIfPrimaryExpression(AIfPrimaryExpression node);
    void caseATimeExpression(ATimeExpression node);
    void caseAPropertyCall(APropertyCall node);
    void caseAPropertyCallParameters(APropertyCallParameters node);
    void caseAActualParameterList(AActualParameterList node);
    void caseANextExpr(ANextExpr node);
    void caseAConcreteDeclarator(AConcreteDeclarator node);
    void caseAAccumulator(AAccumulator node);
    void caseANameList(ANameList node);
    void caseANextName(ANextName node);
    void caseASimpleTypePostfix(ASimpleTypePostfix node);
    void caseAQualifiers(AQualifiers node);
    void caseALogicalOperator(ALogicalOperator node);
    void caseAImplicativeLogicalOperator(AImplicativeLogicalOperator node);
    void caseAAndBooleanOperator(AAndBooleanOperator node);
    void caseAOrBooleanOperator(AOrBooleanOperator node);
    void caseAXorBooleanOperator(AXorBooleanOperator node);
    void caseAImpliesOperator(AImpliesOperator node);
    void caseAEqualityRelationalOperator(AEqualityRelationalOperator node);
    void caseACompareRelationalOperator(ACompareRelationalOperator node);
    void caseAEquationOperator(AEquationOperator node);
    void caseAInEquationOperator(AInEquationOperator node);
    void caseAGtCompareOperator(AGtCompareOperator node);
    void caseALtCompareOperator(ALtCompareOperator node);
    void caseAGteqCompareOperator(AGteqCompareOperator node);
    void caseALteqCompareOperator(ALteqCompareOperator node);
    void caseAPlusAddOperator(APlusAddOperator node);
    void caseAMinusAddOperator(AMinusAddOperator node);
    void caseAMultMultiplyOperator(AMultMultiplyOperator node);
    void caseADivMultiplyOperator(ADivMultiplyOperator node);
    void caseAMinusUnaryOperator(AMinusUnaryOperator node);
    void caseANotUnaryOperator(ANotUnaryOperator node);
    void caseAOclAnyTypeSpecifier(AOclAnyTypeSpecifier node);
    void caseACollectionTypeSpecifier(ACollectionTypeSpecifier node);
    void caseASimpleTypeSpecifier(ASimpleTypeSpecifier node);
    void caseACollectionType(ACollectionType node);
    void caseASetCollectionKind(ASetCollectionKind node);
    void caseABagCollectionKind(ABagCollectionKind node);
    void caseASequenceCollectionKind(ASequenceCollectionKind node);
    void caseACollectionKind(ACollectionKind node);
    void caseALiteralCollection(ALiteralCollection node);
    void caseACollectionItemList(ACollectionItemList node);
    void caseANextCollectionItem(ANextCollectionItem node);
    void caseACollectionItem(ACollectionItem node);
    void caseARange(ARange node);
    void caseAStringLiteral(AStringLiteral node);
    void caseANumberLiteral(ANumberLiteral node);
    void caseABooleanLiteral(ABooleanLiteral node);
    void caseAName(AName node);
    void caseAPathName(APathName node);
    void caseANameQualifier(ANameQualifier node);

    void caseTWhiteSpace(TWhiteSpace node);
    void caseTEndOfLineComment(TEndOfLineComment node);
    void caseTSet(TSet node);
    void caseTBag(TBag node);
    void caseTSequence(TSequence node);
    void caseTCollection(TCollection node);
    void caseTDot(TDot node);
    void caseTArrow(TArrow node);
    void caseTNot(TNot node);
    void caseTMult(TMult node);
    void caseTDiv(TDiv node);
    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTContext(TContext node);
    void caseTPre(TPre node);
    void caseTPost(TPost node);
    void caseTInv(TInv node);
    void caseTDef(TDef node);
    void caseTEqual(TEqual node);
    void caseTNEqual(TNEqual node);
    void caseTLt(TLt node);
    void caseTGt(TGt node);
    void caseTLteq(TLteq node);
    void caseTGteq(TGteq node);
    void caseTAnd(TAnd node);
    void caseTOr(TOr node);
    void caseTXor(TXor node);
    void caseTImplies(TImplies node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTLBracket(TLBracket node);
    void caseTRBracket(TRBracket node);
    void caseTLBrace(TLBrace node);
    void caseTRBrace(TRBrace node);
    void caseTSemicolon(TSemicolon node);
    void caseTDcolon(TDcolon node);
    void caseTColon(TColon node);
    void caseTComma(TComma node);
    void caseTAt(TAt node);
    void caseTBar(TBar node);
    void caseTDdot(TDdot node);
    void caseTIf(TIf node);
    void caseTThen(TThen node);
    void caseTElse(TElse node);
    void caseTEndif(TEndif node);
    void caseTBooleanLiteral(TBooleanLiteral node);
    void caseTLet(TLet node);
    void caseTIn(TIn node);
    void caseTNumberLiteral(TNumberLiteral node);
    void caseTStringLiteral(TStringLiteral node);
    void caseTIdentifier(TIdentifier node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}

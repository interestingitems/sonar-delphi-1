/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.pmd.rules;

import org.apache.commons.lang.StringUtils;
import org.antlr.runtime.tree.Tree;
import org.sonar.plugins.delphi.antlr.DelphiLexer;
import org.sonar.plugins.delphi.antlr.ast.DelphiPMDNode;

/**
 * Class that checks if 'inherited' statement is in some function or procedure. If no, it triggers a violation.
 */
public class NoInheritedStatementRule extends DelphiRule {

  private static final int MAX_LOOK_AHEAD = 3;

  @Override
  public Object visit(DelphiPMDNode node, Object data) {
    String lookFor = getStringProperty("lookFor");
    if (StringUtils.isEmpty(lookFor)) {
      return data;
    }

    if (node.getText().equalsIgnoreCase(lookFor)) {
      Tree beginNode = null;
      for (int i = node.getChildIndex() + 1; i < node.getChildIndex() + MAX_LOOK_AHEAD && i < node.getParent().getChildCount(); ++i) // look
                                                                                                                                     // for
                                                                                                                                     // begin
      {
        if (node.getParent().getChild(i).getType() == DelphiLexer.BEGIN) {
          beginNode = node.getParent().getChild(i);
          break;
        }
      }
      if (beginNode != null) {
        boolean wasInherited = false;
        for (int c = 0; c < beginNode.getChildCount(); c++) {
          if (beginNode.getChild(c).getType() == DelphiLexer.INHERITED) {
            wasInherited = true;
            break;
          }
        }

        if ( !wasInherited) {
          addViolation(data, node);
        }
      }
    }

    return data;
  }

}

/*
 * Copyright 2008 Udai Gupta, Ralf Joachim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.castor.cpa.query.object.condition;

import org.castor.cpa.query.Condition;

/**
 * Final class that represents and compound condition.
 * 
 * @author <a href="mailto:mailtoud AT gmail DOT com">Udai Gupta</a>
 * @author <a href="mailto:ralf DOT joachim AT syscon DOT eu">Ralf Joachim</a>
 * @version $Revision$ $Date$
 * @since 1.3
 */
public final class And extends CompoundCondition {
    //--------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    protected String getOperator() { return " AND "; }
    
    /**
     * {@inheritDoc}
     */
    public Condition and(final Condition condition) {
        if (condition instanceof And) {
            addAllConditions(((And) condition).getConditions());
        } else {
            addCondition(condition);
        }
        return this;
    }

    //--------------------------------------------------------------------------
}

/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 *****************************************************************************/

package codelets.perception;



import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.Collections;
import memory.CreatureInnerSense;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;

/**
 * @author klaus
 *
 */
public class ClosestJewelDetector extends Codelet {

	private MemoryObject knownMO;
	private MemoryObject closestJewelMO;
	private MemoryObject innerSenseMO;
	
        private List<Thing> known;

	public ClosestJewelDetector() {
	}


	@Override
	public void accessMemoryObjects() {
		this.knownMO=(MemoryObject)this.getInput("KNOWN_JEWELS");
		this.innerSenseMO=(MemoryObject)this.getInput("INNER");
		this.closestJewelMO=(MemoryObject)this.getOutput("CLOSEST_JEWEL");	
	}
	@Override
	public void proc() {
                Thing closest_Jewel=null;
                known = Collections.synchronizedList((List<Thing>) knownMO.getI());
                CreatureInnerSense cis = (CreatureInnerSense) innerSenseMO.getI();
                synchronized(known) {
		   if(known.size() != 0){
			//Iterate over objects in vision, looking for the closest apple
                        CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                        for (Thing t : myknown) {
				String objectName=t.getName();
                                //System.out.println("ThingName:" + t.getName());
                                //System.out.println("ThingColor:" + t.getAttributes().getColor());
//                                for (int i = 0; i < cis.LEAF.size(); i++) {
//                                System.out.println("Leafet Remaining:" + cis.LEAF.get(i));
//                            }
//                                System.out.println("-----------------");
				if(objectName.contains("Jewel") && cis.LEAF.contains(t.getAttributes().getColor())){ //Then, it is an jewel
                                        if(closest_Jewel == null){    
                                                closest_Jewel = t;
					}
                                        else {
						double Dnew = calculateDistance(t.getX1(), t.getY1(), cis.position.getX(), cis.position.getY());
                                                double Dclosest= calculateDistance(closest_Jewel.getX1(), closest_Jewel.getY1(), cis.position.getX(), cis.position.getY());
						if(Dnew<Dclosest){
                                                        closest_Jewel = t;
						}
					}
				}
			}
                        
                        if(closest_Jewel!=null){    
				if(closestJewelMO.getI() == null || !closestJewelMO.getI().equals(closest_Jewel)){
                                      closestJewelMO.setI(closest_Jewel);
				}
				
			}else{
				//couldn't find any nearby jewels
                                closest_Jewel = null;
                                closestJewelMO.setI(closest_Jewel);
			}
		   }
                   else  { // if there are no known apples closest_jewel must be null
                        closest_Jewel = null;
                        closestJewelMO.setI(closest_Jewel);
		   }
                }
	}//end proc

@Override
        public void calculateActivation() {
        
        }
        
        private double calculateDistance(double x1, double y1, double x2, double y2) {
            return(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
        }

}

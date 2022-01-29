package gtPlusPlus.xmod.gregtech.common.tileentities.automation;

import java.util.ArrayList;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicTank;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.xmod.gregtech.api.gui.automation.GT_Container_ElectricAutoWorkbench;
import gtPlusPlus.xmod.gregtech.api.gui.automation.GT_GUIContainer_ElectricAutoWorkbench;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class GT_MetaTileEntity_ElectricAutoWorkbench extends GT_MetaTileEntity_BasicTank {
	
	public int mMode = 0, mCurrentSlot = 0, mThroughPut = 0, mTicksUntilNextUpdate = 20;
	public boolean mLastCraftSuccessful = false;
	protected String mLocalName;
	
	public GT_MetaTileEntity_ElectricAutoWorkbench(final int aID, final int aTier, final String aDescription) {
		super(aID, "basicmachine.automation.autoworkbench.0"+aTier, "Auto Workbench ("+GT_Values.VN[aTier]+")", aTier, 30, aDescription);
		mLocalName = "Auto Workbench ("+GT_Values.VN[aTier]+")";
	}

	public GT_MetaTileEntity_ElectricAutoWorkbench(final String aName, final int aTier, final String aDescription, final ITexture[][][] aTextures) {
		super(aName, aTier, 30, aDescription, aTextures);
	}

	@Override
	public Object getServerGUI(final int aID, final InventoryPlayer aPlayerInventory, final IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_Container_ElectricAutoWorkbench(aPlayerInventory, aBaseMetaTileEntity);
	}

	@Override
	public Object getClientGUI(final int aID, final InventoryPlayer aPlayerInventory, final IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_GUIContainer_ElectricAutoWorkbench(aPlayerInventory, aBaseMetaTileEntity);
	}
	
	@Override
	public boolean isTransformerUpgradable() {
		return true;
	}
	
	@Override
	public boolean isOverclockerUpgradable() {
		return false;
	}
	
	@Override
	public boolean isSimpleMachine() {
		return false;
	}
	
	@Override
	public boolean isValidSlot(int aIndex) {
		return aIndex < 19;
	}
	
	@Override
	public boolean isFacingValid(byte aFacing) {
		return true;
	}
	
	@Override
	public boolean isEnetInput() {
		return true;
	}
	
	@Override
	public boolean isEnetOutput() {
		return true;
	}
	
	@Override
	public boolean isInputFacing(byte aSide) {
		return !isOutputFacing(aSide);
	}
	
	@Override
	public boolean isOutputFacing(byte aSide) {
		return aSide == getBaseMetaTileEntity().getBackFacing();
	}
	
	@Override
	public boolean isTeleporterCompatible() {
		return false;
	}
	
	@Override
	public long maxEUInput() {
		return GT_Values.V[mTier];
	}
	
	@Override
	public long maxEUOutput() {
		return mThroughPut % 2 == 0 ? GT_Values.V[mTier] : 0;
	}
	
	@Override
	public long getMinimumStoredEU() {
		return GT_Values.V[this.mTier];
	}
	
	@Override
	public long maxEUStore() {
		return GT_Values.V[this.mTier] * (this.mTier * GT_Values.V[this.mTier]);
	}
	
	@Override
	public int getSizeInventory() {
		return 30;
	}
	
	@Override
	public boolean isAccessAllowed(EntityPlayer aPlayer) {
		return true;
	}
    
	@Override
	public boolean onRightclick(final IGregTechTileEntity aBaseMetaTileEntity, final EntityPlayer aPlayer) {
		if (aBaseMetaTileEntity.isClientSide()) {
			return true;
		}
		aBaseMetaTileEntity.openGUI(aPlayer);
		return true;
	}
	
	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_ElectricAutoWorkbench(this.mName, this.mTier, this.mDescription, this.mTextures);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound aNBT) {
		super.saveNBTData(aNBT);
    	aNBT.setInteger("mMode", mMode);
    	aNBT.setInteger("mThroughPut", mThroughPut);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound aNBT) {
		super.loadNBTData(aNBT);
		mMode = aNBT.getInteger("mMode");
		mThroughPut = aNBT.getInteger("mThroughPut");
	}
	
	@Override
	public boolean doesFillContainers() {
		return false;
	}
	
	@Override
	public boolean doesEmptyContainers() {
		return false;
	}
	
	@Override
	public boolean canTankBeFilled() {
		return true;
	}
	
	@Override
	public boolean canTankBeEmptied() {
		return true;
	}
	
	@Override
	public boolean displaysItemStack() {
		return false;
	}
	
	@Override
	public boolean displaysStackSize() {
		return false;
	}
	
	@Override
	public boolean allowCoverOnSide(byte aSide, GT_ItemStack aStack) {
		return aSide != getBaseMetaTileEntity().getFrontFacing() && aSide != getBaseMetaTileEntity().getBackFacing();
	}
	
	private static final int MAX_MODES = 10;
	
	public void switchModeForward() {
		mMode = (mMode + 1) % MAX_MODES;
		switchMode();
	}

	public void switchModeBackward() {
		mMode--;
		if (mMode < 0) mMode = MAX_MODES-1;
		switchMode();
	}

	private void switchMode() {
		mInventory[28] = null;
	}
	
	public void switchThrough() {
		mThroughPut = (mThroughPut + 1) % 4;
	}
	
	@Override
	public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
		super.onPostTick(aBaseMetaTileEntity, aTick);
		if (getBaseMetaTileEntity().isAllowedToWork() && getBaseMetaTileEntity().isServerSide() && getBaseMetaTileEntity().getUniversalEnergyStored() >= (mMode==5||mMode==6?128:2048) && (getBaseMetaTileEntity().hasWorkJustBeenEnabled() || --mTicksUntilNextUpdate<1)) {
			mTicksUntilNextUpdate = 32;
			
			for (byte i = 19; i < 28; i++) {
				if (mInventory[i] != null && mInventory[i].isItemStackDamageable() && mInventory[i].getItem().hasContainerItem()) {
					mInventory[i].setItemDamage(OreDictionary.WILDCARD_VALUE);
				}
			}
			
			if (mInventory[18] == null) {
				for (byte i = 0; i < 18 && mFluid != null; i++) {
					ItemStack tOutput = GT_Utility.fillFluidContainer(mFluid, mInventory[i], false, true);
					if (tOutput != null) {
						for (byte j = 0; j < 9; j++) {
							if (mInventory[j] == null || (GT_Utility.areStacksEqual(tOutput, mInventory[j]) && mInventory[j].stackSize + tOutput.stackSize <= tOutput.getMaxStackSize())) {
								mFluid.amount -= GT_Utility.getFluidForFilledItem(tOutput, true).amount * tOutput.stackSize;
								getBaseMetaTileEntity().decrStackSize(i, 1);
								if (mInventory[j] == null) {
									mInventory[j] = tOutput;
								} else {
									mInventory[j].stackSize++;
								}
								if (mFluid.amount <= 0) mFluid = null;
								break;
							}
						}
					}
				}
				
				ItemStack[] tRecipe = new ItemStack[9];
				ItemStack tTempStack = null, tOutput = null;
				
				if (mInventory[17] != null && mThroughPut < 2 && mMode != 0) {
					if (mInventory[18] == null) {
						mInventory[18] = mInventory[17];
						mInventory[17] = null;
					}
				} else {
					if (!mLastCraftSuccessful) {
						mCurrentSlot = (mCurrentSlot+1)%18;
						for (int i = 0; i < 17 && mInventory[mCurrentSlot] == null; i++)
							mCurrentSlot = (mCurrentSlot+1)%18;
					}
					switch (mMode) {
					case 0:
						if (mInventory[mCurrentSlot] != null && !isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2 && mCurrentSlot < 8) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						for (int i = 0; i < 9; i++) {
							tRecipe[i] = mInventory[i+19];
							if (tRecipe[i] != null) {
								tRecipe[i] = GT_Utility.copy(tRecipe[i]);
								tRecipe[i].stackSize = 1;
							}
						}
						break;
					case 1:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							tRecipe[1] = tTempStack;
							tRecipe[3] = tTempStack;
							tRecipe[4] = tTempStack;
						} else break;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							tRecipe[2] = tTempStack;
							tRecipe[5] = tTempStack;
							tRecipe[6] = tTempStack;
							tRecipe[7] = tTempStack;
							tRecipe[8] = tTempStack;
						} else break;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 2:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 3:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						tRecipe[1] = tTempStack;
						tRecipe[3] = tTempStack;
						tRecipe[4] = tTempStack;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 4:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						tRecipe[1] = tTempStack;
						tRecipe[2] = tTempStack;
						tRecipe[3] = tTempStack;
						tRecipe[4] = tTempStack;
						tRecipe[5] = tTempStack;
						tRecipe[6] = tTempStack;
						tRecipe[7] = tTempStack;
						tRecipe[8] = tTempStack;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 5:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						
						tOutput = GT_OreDictUnificator.get(true, tTempStack);
						
						if (tOutput != null && GT_Utility.areStacksEqual(tOutput, tTempStack)) tOutput = null;
						
						if (tOutput == null) {
							tRecipe[0] = null;
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
						}
						break;
					case 6:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						} else if (OrePrefixes.dustSmall.contains(mInventory[mCurrentSlot])) {
							tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
							tTempStack.stackSize = 1;
							tRecipe[0] = tTempStack;
							tRecipe[1] = tTempStack;
							tRecipe[3] = tTempStack;
							tRecipe[4] = tTempStack;
							if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
								if (mInventory[18] == null) {
									mInventory[18] = mInventory[mCurrentSlot];
									mInventory[mCurrentSlot] = null;
									mTicksUntilNextUpdate = 1;
								}
								break;
							}
						} else if (OrePrefixes.dustTiny.contains(mInventory[mCurrentSlot])) {
							tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
							tTempStack.stackSize = 1;
							tRecipe[0] = tTempStack;
							tRecipe[1] = tTempStack;
							tRecipe[2] = tTempStack;
							tRecipe[3] = tTempStack;
							tRecipe[4] = tTempStack;
							tRecipe[5] = tTempStack;
							tRecipe[6] = tTempStack;
							tRecipe[7] = tTempStack;
							tRecipe[8] = tTempStack;
							if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
								if (mInventory[18] == null) {
									mInventory[18] = mInventory[mCurrentSlot];
									mInventory[mCurrentSlot] = null;
									mTicksUntilNextUpdate = 1;
								}
								break;
							}
						} else {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 7:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot]) || !OrePrefixes.nugget.contains(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						tRecipe[0] = tTempStack;
						tRecipe[1] = tTempStack;
						tRecipe[2] = tTempStack;
						tRecipe[3] = tTempStack;
						tRecipe[4] = tTempStack;
						tRecipe[5] = tTempStack;
						tRecipe[6] = tTempStack;
						tRecipe[7] = tTempStack;
						tRecipe[8] = tTempStack;
						if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
							if (mInventory[18] == null) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						break;
					case 8:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot]) || mInventory[mCurrentSlot].getItemDamage() <= 0 || !mInventory[mCurrentSlot].getItem().isRepairable()) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						tTempStack = GT_Utility.copy(mInventory[mCurrentSlot]);
						tTempStack.stackSize = 1;
						for (int i = mCurrentSlot + 1; i < 18; i++) {
							if (mInventory[i] != null && mInventory[i].getItem() == tTempStack.getItem() && mInventory[mCurrentSlot].getItemDamage()+mInventory[i].getItemDamage()>tTempStack.getMaxDamage()) {
								tRecipe[0] = tTempStack;
								tRecipe[1] = GT_Utility.copy(mInventory[i]);
								if (GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null) {
									if (mInventory[18] == null) {
										mInventory[18] = mInventory[mCurrentSlot];
										mInventory[mCurrentSlot] = null;
										mTicksUntilNextUpdate = 1;
									}
								}
								break;
							}
						}
						break;
					case 9:
						if (isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(mInventory[mCurrentSlot])) {
							if (mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mCurrentSlot];
								mInventory[mCurrentSlot] = null;
								mTicksUntilNextUpdate = 1;
							}
							break;
						}
						for (byte i = 0, j = 0; i < 18 && j < 9 && (j < 2 || GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe) == null); i++) {
							tRecipe[j] = mInventory[(mCurrentSlot+i)%18];
							if (tRecipe[j] != null) {
								tRecipe[j] = GT_Utility.copy(tRecipe[j]);
								tRecipe[j].stackSize = 1;
								j++;
							}
						}
						if (tRecipe[1] == null) tRecipe[0] = null;
						break;
					}
				}
				
				if (tOutput == null) tOutput = GT_ModHandler.getAllRecipeOutput(getBaseMetaTileEntity().getWorld(), tRecipe);
				
				if (tOutput != null || mMode == 0) mInventory[28] = tOutput;
				
				if (tOutput == null) {
					mLastCraftSuccessful = false;
				} else {
					if ((tTempStack = GT_OreDictUnificator.get(true, tOutput)) != null) {
						tTempStack.stackSize = tOutput.stackSize;
						tOutput = tTempStack;
					}
					
					mInventory[28] = GT_Utility.copy(tOutput);
					ArrayList<ItemStack> tList = recipeContent(tRecipe), tContent = benchContent();
					if (tList.size() > 0 && tContent.size() > 0) {
						
						boolean success = (mMode==6||mMode==7||mInventory[17]==null);
						for (byte i = 0; i < tList.size() && success; i++) {
							success = false;
							for (byte j = 0; j < tContent.size() && !success; j++) {
								if (GT_Utility.areStacksEqual(tList.get(i), tContent.get(j))) {
									if (tList.get(i).stackSize <= tContent.get(j).stackSize) {
										success = true;
									}
								}
							}
						}
						
						if (success) {
							mLastCraftSuccessful = true;
							
							for (byte i = 8; i > -1; i--) {
								for (byte j = 17; j > -1; j--) {
									if (tRecipe[i] != null && mInventory[j] != null) {
										if (GT_Utility.areStacksEqual(tRecipe[i], mInventory[j])) {
											ItemStack tStack = GT_Utility.getContainerItem(mInventory[j], true);
											if (tStack != null) {
												getBaseMetaTileEntity().decrStackSize(j, 1);
												if (!tStack.isItemStackDamageable() || tStack.getItemDamage() < tStack.getMaxDamage()) {
													for (byte k = 9; k < 18; k++) {
														if (mInventory[k] == null) {
															mInventory[k] = GT_Utility.copy(tStack);
															break;
														} else if (GT_Utility.areStacksEqual(mInventory[k], tStack) && mInventory[k].stackSize + tStack.stackSize <= tStack.getMaxStackSize()) {
															mInventory[k].stackSize += tStack.stackSize;
															break;
														}
													}
												}
											} else {
												getBaseMetaTileEntity().decrStackSize(j, 1);
											}
											break;
										}
									}
								}
							}
							
							mInventory[18] = GT_Utility.copy(tOutput);
							getBaseMetaTileEntity().decreaseStoredEnergyUnits(mMode==5||mMode==6||mMode==7?128:2048, true);
							mTicksUntilNextUpdate = 1;
						} else {
							mLastCraftSuccessful = false;
							if (mInventory[mMode==0?8:17] != null && mInventory[18] == null && mThroughPut < 2) {
								mInventory[18] = mInventory[mMode==0?8:17];
								mInventory[mMode==0?8:17] = null;
								mTicksUntilNextUpdate = 1;
							}
						}
					}
					
					if (mInventory[18] == null && mThroughPut < 2) {
						for (byte i = 0; i < 8; i++) {
							for (byte j = i; ++j < 9;) {
								if (GT_Utility.areStacksEqual(mInventory[i], mInventory[j]) && mInventory[i].getMaxStackSize() > 8) {
									mInventory[18] = mInventory[j];
									mInventory[j] = null;
									mTicksUntilNextUpdate = 1;
									break;
								}
							}
						}
					}
				}
			}
			
			if (mThroughPut < 2) {
				getBaseMetaTileEntity().decreaseStoredEnergyUnits(GT_Utility.moveOneItemStack(getBaseMetaTileEntity(), getBaseMetaTileEntity().getIInventoryAtSide(getBaseMetaTileEntity().getBackFacing()), getBaseMetaTileEntity().getBackFacing(), getBaseMetaTileEntity().getFrontFacing(), null, false, (byte)64, (byte)1, (byte)64, (byte)1)*10, true);
			}
		}
	}
	
	private boolean isItemTypeOrItsEmptyLiquidContainerInCraftingGrid(ItemStack aStack) {
		if (aStack == null) return true;
		for (byte i = 19; i < 28; i++) {
			if (mInventory[i] != null) {
				if (GT_Utility.areStacksEqual(mInventory[i], aStack)) return true;
				if (GT_Utility.areStacksEqual(GT_Utility.getContainerForFilledItem(mInventory[i], true), aStack)) return true;
			}
		}
		return false;
	}
	
	private ArrayList<ItemStack> recipeContent(ItemStack[] tRecipe) {
		ArrayList<ItemStack> tList = new ArrayList<ItemStack>();
		for (byte i = 0; i < 9; i++) {
			if (tRecipe[i] != null) {
				boolean temp = false;
				for (byte j = 0; j < tList.size(); j++) {
					if (GT_Utility.areStacksEqual(tRecipe[i], tList.get(j))) {
						tList.get(j).stackSize++;
						temp = true;
						break;
					}
				}
				if (!temp) tList.add(GT_Utility.copy(1, tRecipe[i]));
			}
		}
		return tList;
	}
	
	private ArrayList<ItemStack> benchContent() {
		ArrayList<ItemStack> tList = new ArrayList<ItemStack>();
		for (byte i = 0; i < 18; i++) {
			if (mInventory[i] != null) {
				boolean temp = false;
				for (byte j = 0; j < tList.size(); j++) {
					if (GT_Utility.areStacksEqual(mInventory[i], mInventory[j])) {
						tList.get(j).stackSize += mInventory[i].stackSize;
						temp = true;
						break;
					}
				}
				if (!temp) tList.add(GT_Utility.copy(mInventory[i]));
			}
		}
		return tList;
	}
	
	@Override
	public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		return mMode==0?aIndex>=10:aIndex>=18;
	}

	@Override
	public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
		return mMode==0?aIndex<9:aIndex<18;
	}
	
	/*@Override
	public int getTextureIndex(byte aSide, byte aFacing, boolean aActive, boolean aRedstone) {
		if (aSide == aFacing)
			return 112;
		if (GT_Utility.getOppositeSide(aSide) == aFacing)
			return 113;
		return 114;
	}*/
	
	@Override
	public int getCapacity() {
		return 16000;
	}
	
	@Override
	public int getTankPressure() {
		return -100;
	}

	@Override
	public String[] getDescription() {
		return new String[] { 
				"Automatic Crafting Table Mk III",
				//this.mDescription, 
				CORE.GT_Tooltip };
	}
	
	@Override
	public ITexture[][][] getTextureSet(final ITexture[] aTextures) {
		final ITexture[][][] rTextures = new ITexture[10][17][];
		for (byte i = -1; i < 16; i++) {
			rTextures[0][i + 1] = this.getFront(i);
			rTextures[1][i + 1] = this.getBack(i);
			rTextures[2][i + 1] = this.getBottom(i);
			rTextures[3][i + 1] = this.getTop(i);
			rTextures[4][i + 1] = this.getSides(i);
			rTextures[5][i + 1] = this.getFront(i);
			rTextures[6][i + 1] = this.getBack(i);
			rTextures[7][i + 1] = this.getBottom(i);
			rTextures[8][i + 1] = this.getTop(i);
			rTextures[9][i + 1] = this.getSides(i);
		}
		return rTextures;
	}

	@Override
	public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final byte aSide, final byte aFacing,
			final byte aColorIndex, final boolean aActive, final boolean aRedstone) {
		if (aSide == aFacing) {
			return this.mTextures[0][aColorIndex + 1];
		}
		else if (GT_Utility.getOppositeSide(aSide) == aFacing) {
			return this.mTextures[1][aColorIndex + 1];
		}
		else {
			return this.mTextures[4][aColorIndex + 1];			
		}
		/*return this.mTextures[(aActive ? 5 : 0) + (aSide == aFacing ? 0
				: aSide == GT_Utility.getOppositeSide(aFacing) ? 1 : aSide == 0 ? 2 : aSide == 1 ? 3 : 4)][aColorIndex + 1];*/
	}

	public ITexture[] getFront(final byte aColor) {
		return new ITexture[] {Textures.BlockIcons.MACHINE_CASINGS[mTier][aColor + 1], new GT_RenderedTexture(TexturesGtBlock.Casing_Adv_Workbench_Crafting_Overlay)};
	}

	public ITexture[] getBack(final byte aColor) {
		return new ITexture[] {Textures.BlockIcons.MACHINE_CASINGS[mTier][aColor + 1], new GT_RenderedTexture(BlockIcons.OVERLAY_PIPE)};
	}

	public ITexture[] getBottom(final byte aColor) {
		return new ITexture[] {Textures.BlockIcons.MACHINE_CASINGS[mTier][aColor + 1], };
	}

	public ITexture[] getTop(final byte aColor) {
		return new ITexture[] { Textures.BlockIcons.MACHINE_CASINGS[mTier][aColor + 1],	new GT_RenderedTexture(TexturesGtBlock.Casing_Adv_Workbench_Crafting_Overlay) };
	}

	public ITexture[] getSides(final byte aColor) {
		return new ITexture[] {Textures.BlockIcons.MACHINE_CASINGS[mTier][aColor + 1], new GT_RenderedTexture(TexturesGtBlock.Casing_Adv_Workbench_Crafting_Overlay)};
	}
}
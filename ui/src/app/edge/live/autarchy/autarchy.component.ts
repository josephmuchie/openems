import { AutarchyModalComponent } from './modal/modal.component';
import { Component } from '@angular/core';
import { ChannelAddress, CurrentData, Edge, Utils } from '../../../shared/shared';
import { AbstractFlatWidget } from '../flat/abstract-flat-widget';

@Component({
  selector: 'autarchy',
  templateUrl: './autarchy.component.html'
})
export class AutarchyComponent extends AbstractFlatWidget {

  public edge: Edge = null;
  public percentageValue: number;
  private gridActivePowerChannel: ChannelAddress;
  private consumptionActivePowerChannel: ChannelAddress;

  protected getChannelAddresses(): ChannelAddress[] {
    this.gridActivePowerChannel = new ChannelAddress('_sum', 'GridActivePower');
    this.consumptionActivePowerChannel = new ChannelAddress('_sum', 'ConsumptionActivePower')
    let channelAddresses: ChannelAddress[] = [this.gridActivePowerChannel, this.consumptionActivePowerChannel]
    return channelAddresses;
  }

  protected onCurrentData(currentData: CurrentData) {
    this.percentageValue = this.calculateAutarchy(currentData.allComponents[this.gridActivePowerChannel.toString()], currentData.allComponents[this.consumptionActivePowerChannel.toString()]);
  }

  private calculateAutarchy(buyFromGrid: number, consumptionActivePower: number): number | null {
    if (buyFromGrid != null && consumptionActivePower != null) {
      let result = Math.max(
        Utils.orElse(
          (
            1 - (
              Utils.divideSafely(
                Utils.orElse(buyFromGrid, 0),
                Math.max(Utils.orElse(consumptionActivePower, 0), 0)
              )
            )
          ) * 100, 0
        ), 0)
      if (result > 100) {
        return 100;
      }
      return result;
    } else {
      return null
    }

  }

  async presentModal() {
    const modal = await this.modalController.create({
      component: AutarchyModalComponent,
    });
    return await modal.present();
  }

}

package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

public class PublishConfrenceBroadcast implements Broadcast {
    private List<Model> goodModels;
    public PublishConfrenceBroadcast(List<Model> goodModels) {
        this.goodModels =goodModels;
    }

    public List<Model> getGoodModels() {
        return goodModels;
    }
}

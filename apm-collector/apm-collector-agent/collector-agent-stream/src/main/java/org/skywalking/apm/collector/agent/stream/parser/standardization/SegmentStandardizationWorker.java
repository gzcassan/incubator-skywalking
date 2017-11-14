/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.agent.stream.parser.standardization;

import org.skywalking.apm.collector.agent.stream.buffer.SegmentBufferManager;
import org.skywalking.apm.collector.core.module.ModuleManager;
import org.skywalking.apm.collector.queue.service.QueueCreatorService;
import org.skywalking.apm.collector.stream.worker.base.AbstractLocalAsyncWorker;
import org.skywalking.apm.collector.stream.worker.base.AbstractLocalAsyncWorkerProvider;
import org.skywalking.apm.collector.stream.worker.base.WorkerException;
import org.skywalking.apm.network.proto.UpstreamSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng-yongsheng
 */
public class SegmentStandardizationWorker extends AbstractLocalAsyncWorker<UpstreamSegment, UpstreamSegment> {

    private final Logger logger = LoggerFactory.getLogger(SegmentStandardizationWorker.class);

    public SegmentStandardizationWorker(ModuleManager moduleManager) {
        super(moduleManager);
        SegmentBufferManager.INSTANCE.initialize();
    }

    @Override public int id() {
        return SegmentStandardizationWorker.class.hashCode();
    }

    @Override protected void onWork(UpstreamSegment upstreamSegment) throws WorkerException {
        SegmentBufferManager.INSTANCE.writeBuffer(upstreamSegment);
    }

    public final void flushAndSwitch() {
        SegmentBufferManager.INSTANCE.flush();
    }

    public static class Factory extends AbstractLocalAsyncWorkerProvider<UpstreamSegment, UpstreamSegment, SegmentStandardizationWorker> {
        public Factory(ModuleManager moduleManager, QueueCreatorService<UpstreamSegment> queueCreatorService) {
            super(moduleManager, queueCreatorService);
        }

        @Override public SegmentStandardizationWorker workerInstance(ModuleManager moduleManager) {
            return new SegmentStandardizationWorker(moduleManager);
        }

        @Override public int queueSize() {
            return 1024;
        }
    }
}

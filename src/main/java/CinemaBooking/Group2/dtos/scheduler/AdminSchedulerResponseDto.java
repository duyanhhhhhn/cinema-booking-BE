package CinemaBooking.Group2.dtos.scheduler;


import java.util.List;

public class AdminSchedulerResponseDto {
    private String cinemaName;
    private List<AdminSchedulerResourceDto> resources;
    private List<AdminSchedulerEventDto> events;
    
    public AdminSchedulerResponseDto() {}

    public AdminSchedulerResponseDto(String cinemaName, List<AdminSchedulerResourceDto> resources, List<AdminSchedulerEventDto> events) {
        this.cinemaName = cinemaName;
        this.resources = resources;
        this.events = events;
    }
    public List<AdminSchedulerResourceDto> getResources() { return resources; }
    public void setResources(List<AdminSchedulerResourceDto> resources) { this.resources = resources; }

    public List<AdminSchedulerEventDto> getEvents() { return events; }
    public void setEvents(List<AdminSchedulerEventDto> events) { this.events = events; }
    
    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }
}